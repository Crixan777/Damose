package org.damose.controller;

import org.damose.model.*;
import org.damose.model.Shape;
import org.damose.view.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestore del pannello Ricerca.
 *
 * <p>Permette di cercare fermate e linee e visualizzarle sulla mappa.
 * Aggiorna dinamicamente la lista dei risultati mentre l'utente digita.</p>
 *
 * <p>Gestisce:</p>
 * <ul>
 *   <li>Filtraggio in tempo reale dei risultati</li>
 *   <li>Selezione di un risultato per zoom sulla mappa e apertura pannello</li>
 *   <li>Disegno della linea selezionata (andata/ritorno)</li>
 *   <li>Chiusura del pannello laterale</li>
 * </ul>
 */
public class GestorePannelloRicerca {

    private final PannelloRicerca view;
    private final MyFrame frame;
    private final MappaAutobus mappa;

    private final List<Stop> fermate;
    private final List<Route> linee;
    private final List<Trip> trips;
    private final List<Shape> shapes;

    private final List<Object> risultatiFiltrati;

    /**
     * Costruttore.
     *
     * @param view  pannello ricerca
     * @param frame frame principale
     * @param mappa mappa autobus
     */
    public GestorePannelloRicerca(PannelloRicerca view, MyFrame frame, MappaAutobus mappa) {
        this.view = view;
        this.frame = frame;
        this.mappa = mappa;

        this.fermate = mappa.getFermate();
        this.linee = mappa.getLinee();
        this.trips = mappa.getTrips();
        this.shapes = mappa.getShapes();

        this.risultatiFiltrati = new ArrayList<>();

        // registra listener per ricerca, lista e chiusura pannello
        registraListener();
    }

    /**
     * Registra listener per:
     * - aggiornamento lista in tempo reale
     * - selezione di un elemento
     * - chiusura del pannello
     */
    private void registraListener() {
        // aggiornamento lista mentre l'utente digita
        view.getCampoRicerca().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aggiornaLista(); }
            public void removeUpdate(DocumentEvent e) { aggiornaLista(); }
            public void changedUpdate(DocumentEvent e) { aggiornaLista(); }
        });

        // gestione selezione elemento dalla lista
        view.getListaRisultati().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = view.getListaRisultati().getSelectedIndex();
                if (index >= 0) {
                    Object selezionato = risultatiFiltrati.get(index);
                    GestoreMappa.clearPainterLinea(); // rimuove eventuale percorso linea precedente

                    if (selezionato instanceof Stop) {
                        mostraFermata((Stop) selezionato);
                    } else if (selezionato instanceof Route) {
                        mostraLinea((Route) selezionato);
                    }
                }
            }
        });

        // chiusura pannello
        view.getChiudiButton().addActionListener(e -> chiudiPannello());

        // aggiorna inizialmente la lista
        aggiornaLista();
    }

    /**
     * Aggiorna la lista dei risultati filtrando fermate e linee in base al testo.
     */
    private void aggiornaLista() {
        String testo = Optional.ofNullable(view.getCampoRicerca().getText()).orElse("").toLowerCase();

        DefaultListModel<String> modello = view.getModelloLista();
        modello.clear();
        risultatiFiltrati.clear();

        // aggiunge fermate corrispondenti
        for (Stop stop : fermate) {
            if (testo.isEmpty() ||
                    stop.getStopName().toLowerCase().contains(testo) ||
                    stop.getStopId().toLowerCase().contains(testo)) {
                modello.addElement("Fermata: " + stop.getStopName() + " (ID: " + stop.getStopId() + ")");
                risultatiFiltrati.add(stop);
            }
        }

        // aggiunge linee corrispondenti
        for (Route route : linee) {
            String shortName = Optional.ofNullable(route.getRouteShortName()).orElse("").toLowerCase();
            String longName = Optional.ofNullable(route.getRouteLongName()).orElse("").toLowerCase();

            if (testo.isEmpty() || shortName.contains(testo) || longName.contains(testo)) {
                modello.addElement("Linea: " + route.getRouteShortName() + " - " + route.getRouteLongName());
                risultatiFiltrati.add(route);
            }
        }
    }

    /**
     * Mostra la fermata selezionata sulla mappa e apre il pannello fermata.
     */
    private void mostraFermata(Stop stop) {
        GestoreMappa.mostraTuttiWaypoints();
        GestoreMappa.zoomFermata(stop);

        PannelloFermata pannelloFermata = new PannelloFermata(stop);
        new GestorePannelloFermata(pannelloFermata, frame, mappa);
        frame.aggiornaPannello(pannelloFermata, stop);
    }

    /**
     * Mostra la linea selezionata sulla mappa, evidenziando andata e ritorno.
     */
    private void mostraLinea(Route route) {
        List<Trip> tripsDellaLinea = trips.stream()
                .filter(t -> t.getRouteId().equals(route.getRouteId()))
                .collect(Collectors.toList());

        if (tripsDellaLinea.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nessun trip trovato per la linea " + route.getRouteShortName());
            return;
        }

        Trip tripAndata = tripsDellaLinea.get(0);
        Trip tripRitorno = tripsDellaLinea.size() > 1 ? tripsDellaLinea.get(1) : null;

        WaypointPainter<BusWaypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(new HashSet<>(mappa.getWaypoints()));
        waypointPainter.setRenderer(new GestoreMappa.ImageWaypointRenderer<>(GestoreMappa.pinIcon, 52, 52));

        Painter<JXMapViewer> routePainterAndata = creaPainter(getGeoPositions(tripAndata), new Color(0,0,139,220));
        Painter<JXMapViewer> routePainterRitorno = tripRitorno != null ?
                creaPainter(getGeoPositions(tripRitorno), new Color(139,0,0,220)) : null;

        // combina painters
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(waypointPainter);
        painters.add(routePainterAndata);
        if (routePainterRitorno != null) painters.add(routePainterRitorno);

        mappa.getMapViewer().setOverlayPainter(new CompoundPainter<>(painters));

        // zoom automatico sui punti
        Set<GeoPosition> tuttiPunti = new HashSet<>(getGeoPositions(tripAndata));
        if (tripRitorno != null) {
            tuttiPunti.addAll(getGeoPositions(tripRitorno));
        }
        if (!tuttiPunti.isEmpty()) {
            mappa.getMapViewer().zoomToBestFit(tuttiPunti, 0.7);
        }
        mappa.getMapViewer().repaint();

        // pannello linea
        PannelloLinea pannelloLinea = new PannelloLinea(route, mappa, frame);
        new GestorePannelloLinea(pannelloLinea, route, frame);
        frame.aggiornaPannello(pannelloLinea, route);
    }

    /**
     * Crea un painter per disegnare un percorso sulla mappa.
     */
    private Painter<JXMapViewer> creaPainter(List<GeoPosition> geo, Color colore) {
        return (g, map, w, h) -> {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(colore);
            g2.setStroke(new BasicStroke(6));
            Rectangle viewport = map.getViewportBounds();
            Path2D path = new Path2D.Double();
            boolean first = true;
            for (GeoPosition gp : geo) {
                Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
                pt.setLocation(pt.getX() - viewport.getX(), pt.getY() - viewport.getY());
                if (first) {
                    path.moveTo(pt.getX(), pt.getY());
                    first = false;
                } else {
                    path.lineTo(pt.getX(), pt.getY());
                }
            }
            g2.draw(path);
            g2.dispose();
        };
    }

    /**
     * Restituisce le posizioni geografiche del percorso di un trip.
     */
    private List<GeoPosition> getGeoPositions(Trip trip) {
        return shapes.stream()
                .filter(sh -> sh.getShapeId().equals(trip.getShapeId()))
                .sorted(Comparator.comparingInt(Shape::getShapePtSequence))
                .map(sh -> new GeoPosition(sh.getShapePtLat(), sh.getShapePtLon()))
                .collect(Collectors.toList());
    }

    /**
     * Chiude il pannello laterale di ricerca.
     */
    private void chiudiPannello() {
        JPanel pannelloLaterale = (JPanel) frame.getContentPane().getComponent(1); // BorderLayout.WEST
        pannelloLaterale.setPreferredSize(new Dimension(0, frame.getHeight()));
        pannelloLaterale.removeAll();
        pannelloLaterale.revalidate();
        pannelloLaterale.repaint();

        try {
            Field fieldVisibile = MyFrame.class.getDeclaredField("pannelloVisibile");
            fieldVisibile.setAccessible(true);
            fieldVisibile.setBoolean(frame, false);

            Field fieldUltimo = MyFrame.class.getDeclaredField("ultimoElementoPremuto");
            fieldUltimo.setAccessible(true);
            fieldUltimo.set(frame, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
