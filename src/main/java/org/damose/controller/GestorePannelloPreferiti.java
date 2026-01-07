package org.damose.controller;

import org.damose.model.*;
import org.damose.model.Shape;
import org.damose.view.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestore del pannello Preferiti.
 *
 * <p>Gestisce:</p>
 * <ul>
 *     <li>Aggiunta/rimozione di preferiti (fermate o linee)</li>
 *     <li>Ricerca e filtro in tempo reale</li>
 *     <li>Selezione di un elemento e apertura del pannello corrispondente</li>
 *     <li>Visualizzazione della linea sulla mappa (andata/ritorno)</li>
 * </ul>
 */
public class GestorePannelloPreferiti {

    private static final List<Object> preferiti = new ArrayList<>();

    // --- Metodi statici per gestire la lista dei preferiti ---

    public static void aggiungiPreferito(Object obj) {
        if (obj != null && !preferiti.contains(obj)) {
            preferiti.add(obj);
        }
    }

    public static void rimuoviPreferito(Object obj) {
        preferiti.remove(obj);
    }

    public static boolean isPreferito(Object obj) {
        return preferiti.contains(obj);
    }

    public static List<Object> getPreferiti() {
        return Collections.unmodifiableList(preferiti);
    }

    // --- Campi dell'istanza ---
    private final PannelloPreferiti view;
    private final MyFrame frame;
    private final MappaAutobus mappa;

    private final List<Stop> fermate;
    private final List<Route> linee;
    private final List<Trip> trips;
    private final List<Shape> shapes;

    private List<Object> preferitiFiltrati;

    /**
     * Costruttore.
     * @param view Pannello Preferiti
     * @param frame Frame principale
     * @param mappa Mappa autobus
     */
    public GestorePannelloPreferiti(PannelloPreferiti view, MyFrame frame, MappaAutobus mappa) {
        this.view = view;
        this.frame = frame;
        this.mappa = mappa;

        this.fermate = mappa.getFermate();
        this.linee = mappa.getLinee();
        this.trips = mappa.getTrips();
        this.shapes = mappa.getShapes();

        registraListener();
        aggiornaLista();
    }

    /** Registra listener per ricerca, selezione lista e chiusura pannello */
    private void registraListener() {
        // --- Ricerca in tempo reale ---
        view.getCampoRicerca().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aggiornaLista(); }
            public void removeUpdate(DocumentEvent e) { aggiornaLista(); }
            public void changedUpdate(DocumentEvent e) { aggiornaLista(); }
        });

        // --- Selezione elemento preferito ---
        view.getListaPreferiti().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = view.getListaPreferiti().getSelectedIndex();
                if (index >= 0 && preferitiFiltrati != null) {
                    Object selezionato = preferitiFiltrati.get(index);

                    if (selezionato instanceof Stop) {
                        Stop stop = (Stop) selezionato;
                        GestoreMappa.zoomFermata(stop);

                        PannelloFermata pannelloFermata = new PannelloFermata(stop);
                        new GestorePannelloFermata(pannelloFermata, frame, mappa);
                        frame.aggiornaPannello(pannelloFermata, stop);

                    } else if (selezionato instanceof Route) {
                        Route route = (Route) selezionato;
                        mostraLinea(route);
                    }
                }
            }
        });

        // --- Pulsante chiudi pannello ---
        view.getChiudiButton().addActionListener(e -> {
            JPanel pannelloLaterale = (JPanel) frame.getContentPane().getComponent(1); // BorderLayout.WEST
            pannelloLaterale.setPreferredSize(new Dimension(0, frame.getHeight()));
            pannelloLaterale.removeAll();
            pannelloLaterale.revalidate();
            pannelloLaterale.repaint();

            try {
                // reset stato interno del frame tramite reflection
                Field fieldVisibile = MyFrame.class.getDeclaredField("pannelloVisibile");
                fieldVisibile.setAccessible(true);
                fieldVisibile.setBoolean(frame, false);

                Field fieldUltimo = MyFrame.class.getDeclaredField("ultimoElementoPremuto");
                fieldUltimo.setAccessible(true);
                fieldUltimo.set(frame, null);
            } catch (Exception ex) {
                // ignoriamo errori reflection
            }
        });
    }

    /**
     * Mostra una linea sulla mappa:
     * - evidenzia andata (blu) e ritorno (rosso)
     * - mostra waypoint dei bus
     * - apre il pannello della linea
     */
    private void mostraLinea(Route route) {
        // Filtra tutti i trip della linea
        List<Trip> tripsDellaLinea = trips.stream()
                .filter(t -> t.getRouteId().equals(route.getRouteId()))
                .collect(Collectors.toList());

        if (tripsDellaLinea.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nessun trip trovato per la linea " + route.getRouteShortName());
            return;
        }

        Trip tripAndata = tripsDellaLinea.get(0);
        Trip tripRitorno = tripsDellaLinea.size() > 1 ? tripsDellaLinea.get(1) : null;

        // --- Waypoint ---
        WaypointPainter<BusWaypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(new HashSet<>(mappa.getWaypoints()));
        waypointPainter.setRenderer(new GestoreMappa.ImageWaypointRenderer<>(GestoreMappa.pinIcon, 52, 52));

        // --- Disegna percorso andata ---
        List<GeoPosition> geoAndata = shapes.stream()
                .filter(sh -> sh.getShapeId().equals(tripAndata.getShapeId()))
                .sorted(Comparator.comparingInt(Shape::getShapePtSequence))
                .map(sh -> new GeoPosition(sh.getShapePtLat(), sh.getShapePtLon()))
                .collect(Collectors.toList());

        Painter<JXMapViewer> routePainterAndata = creaRoutePainter(geoAndata, new Color(0,0,139,220));

        // --- Disegna percorso ritorno ---
        Painter<JXMapViewer> routePainterRitorno = null;
        if (tripRitorno != null) {
            List<GeoPosition> geoRitorno = shapes.stream()
                    .filter(sh -> sh.getShapeId().equals(tripRitorno.getShapeId()))
                    .sorted(Comparator.comparingInt(Shape::getShapePtSequence))
                    .map(sh -> new GeoPosition(sh.getShapePtLat(), sh.getShapePtLon()))
                    .collect(Collectors.toList());
            routePainterRitorno = creaRoutePainter(geoRitorno, new Color(139,0,0,220));
        }

        // --- Combina painters e applica alla mappa ---
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(waypointPainter);
        painters.add(routePainterAndata);
        if (routePainterRitorno != null) painters.add(routePainterRitorno);

        CompoundPainter<JXMapViewer> compound = new CompoundPainter<>(painters);
        mappa.getMapViewer().setOverlayPainter(compound);

        // Zoom automatico su tutti i punti
        Set<GeoPosition> tuttiPunti = new HashSet<>(geoAndata);
        if (tripRitorno != null) {
            shapes.stream()
                    .filter(sh -> sh.getShapeId().equals(tripRitorno.getShapeId()))
                    .map(sh -> new GeoPosition(sh.getShapePtLat(), sh.getShapePtLon()))
                    .forEach(tuttiPunti::add);
        }

        if (!tuttiPunti.isEmpty()) {
            mappa.getMapViewer().zoomToBestFit(tuttiPunti, 0.7);
        }
        mappa.getMapViewer().repaint();

        // --- Apri pannello linea ---
        PannelloLinea pannelloLinea = new PannelloLinea(route, mappa, frame);
        new GestorePannelloLinea(pannelloLinea, route, frame);
        frame.aggiornaPannello(pannelloLinea, route);
    }

    /** Crea un Painter per disegnare un percorso sulla mappa */
    private Painter<JXMapViewer> creaRoutePainter(List<GeoPosition> geoPositions, Color colore) {
        return (g, map, w, h) -> {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(colore);
            g2.setStroke(new BasicStroke(6));
            Rectangle viewport = map.getViewportBounds();
            Path2D path = new Path2D.Double();
            boolean first = true;
            for (GeoPosition gp : geoPositions) {
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

    /** Aggiorna la lista dei preferiti filtrando per testo di ricerca */
    private void aggiornaLista() {
        String testo = view.getCampoRicerca().getText().toLowerCase();

        preferitiFiltrati = getPreferiti().stream()
                .filter(obj -> {
                    if (obj instanceof Stop) {
                        Stop s = (Stop) obj;
                        return s.getStopName().toLowerCase().contains(testo)
                                || s.getStopId().toLowerCase().contains(testo);
                    } else if (obj instanceof Route) {
                        Route r = (Route) obj;
                        return r.getRouteShortName().toLowerCase().contains(testo)
                                || r.getRouteLongName().toLowerCase().contains(testo);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        DefaultListModel<String> modello = view.getModelloLista();
        modello.clear();
        for (Object obj : preferitiFiltrati) {
            if (obj instanceof Stop) {
                Stop s = (Stop) obj;
                modello.addElement("Fermata: " + s.getStopName() + " (ID: " + s.getStopId() + ")");
            } else if (obj instanceof Route) {
                Route r = (Route) obj;
                modello.addElement("Linea: " + r.getRouteShortName() + " - " + r.getRouteLongName());
            }
        }
    }
}
