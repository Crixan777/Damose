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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

import com.google.transit.realtime.GtfsRealtime;

/**
 * Gestore del pannello di una singola fermata.
 *
 * <p>
 * Questo gestore si occupa di:
 * <ul>
 *     <li>Caricare gli orari offline da file GTFS statici</li>
 *     <li>Aggiornare gli orari in tempo reale tramite feed GTFS-RT</li>
 *     <li>Mostrare linee e percorsi sulla mappa</li>
 *     <li>Gestire preferiti e interazioni dell'utente</li>
 * </ul>
 * </p>
 *
 *
 * @see PannelloFermata
 * @see MyFrame
 * @see MappaAutobus
 */
public class GestorePannelloFermata {

    /** Riferimento al pannello della fermata */
    private final PannelloFermata view;

    /** Fermata associata al pannello */
    private final Stop stop;

    /** Frame principale dell'applicazione */
    private final MyFrame frame;

    /** Mappa contenente linee, trips e waypoint */
    private final MappaAutobus mappa;

    /**
     * Costruttore: inizializza il gestore, registra listener e avvia aggiornamenti automatici.
     *
     * @param view pannello fermata
     * @param frame frame principale
     * @param mappa mappa degli autobus
     */
    public GestorePannelloFermata(PannelloFermata view, MyFrame frame, MappaAutobus mappa) {
        this.view = view;
        this.stop = view.getStop();
        this.frame = frame;
        this.mappa = mappa;

        // Registra i listener sui componenti del pannello
        registraListener();

        // Carica gli orari iniziali offline
        caricaOrariFermata();

        // Registra click sulla tabella per mostrare la linea
        registraSelezioneTabella();

        // Aggiornamento automatico ogni 30 secondi
        Timer timerAggiornamento = new Timer("AggiornaOrariFermata", true);
        timerAggiornamento.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Aggiornamento in Event Dispatch Thread
                SwingUtilities.invokeLater(() -> aggiornaOrariRealtime());
            }
        }, 0, 30_000);

        // Aggiorna automaticamente quando cambia lo stato della rete
        GestoreMode.addStatusListener(this::aggiornaOrariRealtime);
    }

    /**
     * Registra listener su checkbox dei preferiti e sul pulsante chiudi.
     * La checkbox aggiorna i preferiti; il pulsante chiudi resetta il pannello.
     */
    private void registraListener() {
        JCheckBox stella = view.getStellaCheckBox();

        // Aggiunge o rimuove la fermata dai preferiti
        stella.addActionListener(e -> {
            if (stella.isSelected()) {
                GestorePannelloPreferiti.aggiungiPreferito(stop);
            } else {
                GestorePannelloPreferiti.rimuoviPreferito(stop);
            }
        });

        // Chiude il pannello laterale
        view.getChiudiButton().addActionListener(e -> {
            JPanel pannelloLaterale = (JPanel) frame.getContentPane().getComponent(1);
            pannelloLaterale.setPreferredSize(new Dimension(0, frame.getHeight()));
            pannelloLaterale.removeAll();
            pannelloLaterale.revalidate();
            pannelloLaterale.repaint();

            // Reset campi interni tramite reflection
            try {
                Field fieldVisibile = MyFrame.class.getDeclaredField("pannelloVisibile");
                fieldVisibile.setAccessible(true);
                fieldVisibile.setBoolean(frame, false);

                Field fieldUltimo = MyFrame.class.getDeclaredField("ultimoElementoPremuto");
                fieldUltimo.setAccessible(true);
                fieldUltimo.set(frame, null);
            } catch (Exception ignored) {}
        });
    }

    /**
     * Registra listener sulla tabella: click su riga mostra la linea corrispondente sulla mappa.
     */
    private void registraSelezioneTabella() {
        JTable tabella = view.getTabellaOrari();

        tabella.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int riga = tabella.getSelectedRow();
                if (riga < 0) return;

                String linea = (String) tabella.getValueAt(riga, 0);
                if (linea == null || linea.equals("—") || linea.equals("Caricamento...")) return;

                // Cerca la route corrispondente
                Route route = mappa.getLinee().stream()
                        .filter(r -> linea.equals(r.getRouteShortName()))
                        .findFirst().orElse(null);

                if (route != null) {
                    mostraLinea(route);
                }
            }
        });
    }

    /**
     * Mostra la linea selezionata sulla mappa e aggiorna il pannello linea.
     *
     * @param route linea da mostrare
     */
    private void mostraLinea(Route route) {
        // Trova tutti i trip della linea
        List<Trip> tripsDellaLinea = mappa.getTrips().stream()
                .filter(t -> t.getRouteId().equals(route.getRouteId()))
                .collect(Collectors.toList());

        if (tripsDellaLinea.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nessun trip trovato per la linea " + route.getRouteShortName());
            return;
        }

        Trip tripAndata = tripsDellaLinea.get(0);
        Trip tripRitorno = tripsDellaLinea.size() > 1 ? tripsDellaLinea.get(1) : null;

        // Painter dei waypoint
        WaypointPainter<BusWaypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(new HashSet<>(mappa.getWaypoints()));
        waypointPainter.setRenderer(new GestoreMappa.ImageWaypointRenderer<>(GestoreMappa.pinIcon, 52, 52));

        // Painter per andata
        Painter<JXMapViewer> routePainterAndata = creaRoutePainter(tripAndata, Color.BLUE.darker());

        // Painter per ritorno, se presente
        Painter<JXMapViewer> routePainterRitorno = tripRitorno != null ? creaRoutePainter(tripRitorno, Color.RED.darker()) : null;

        // Combina tutti i painters
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(waypointPainter);
        painters.add(routePainterAndata);
        if (routePainterRitorno != null) painters.add(routePainterRitorno);

        CompoundPainter<JXMapViewer> compound = new CompoundPainter<>(painters);
        mappa.getMapViewer().setOverlayPainter(compound);

        // Zoom automatico su tutti i punti
        Set<GeoPosition> tuttiPunti = new HashSet<>();
        tuttiPunti.addAll(mappa.getShapes().stream()
                .filter(sh -> sh.getShapeId().equals(tripAndata.getShapeId()))
                .map(sh -> new GeoPosition(sh.getShapePtLat(), sh.getShapePtLon()))
                .collect(Collectors.toList()));
        if (tripRitorno != null) {
            tuttiPunti.addAll(mappa.getShapes().stream()
                    .filter(sh -> sh.getShapeId().equals(tripRitorno.getShapeId()))
                    .map(sh -> new GeoPosition(sh.getShapePtLat(), sh.getShapePtLon()))
                    .collect(Collectors.toList()));
        }
        if (!tuttiPunti.isEmpty()) {
            mappa.getMapViewer().zoomToBestFit(tuttiPunti, 0.7);
        }

        mappa.getMapViewer().repaint();

        // Mostra pannello linea
        PannelloLinea pannelloLinea = new PannelloLinea(route, mappa, frame);
        new GestorePannelloLinea(pannelloLinea, route, frame);
        frame.aggiornaPannello(pannelloLinea, route);
    }

    /**
     * Crea un Painter per un trip, disegnando la linea sulla mappa con il colore specificato.
     */
    private Painter<JXMapViewer> creaRoutePainter(Trip trip, Color colore) {
        List<Shape> shape = mappa.getShapes().stream()
                .filter(sh -> sh.getShapeId().equals(trip.getShapeId()))
                .sorted(Comparator.comparingInt(Shape::getShapePtSequence))
                .collect(Collectors.toList());

        List<GeoPosition> geo = shape.stream()
                .map(sh -> new GeoPosition(sh.getShapePtLat(), sh.getShapePtLon()))
                .collect(Collectors.toList());

        return (g, map, w, h) -> {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(colore.getRed(), colore.getGreen(), colore.getBlue(), 220));
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
     * Carica gli orari della fermata dai file GTFS statici.
     * <p>
     * Esegue il parsing in background con SwingWorker e aggiorna la tabella.
     * </p>
     */
    private void caricaOrariFermata() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        model.addRow(new Object[]{"Caricamento...", ""});

        new SwingWorker<List<StopTime>, Void>() {
            @Override
            public List<StopTime> doInBackground() {
                try {
                    LocalTime oraAttuale = LocalTime.now();
                    LocalTime oraFine = oraAttuale.plusMinutes(40);
                    // Parsing del file stop_times.txt filtrando per 40 minuti
                    return MyParser.parseStopTimesByHour(
                            "src/main/resources/gtfsStatici/stop_times.txt",
                            oraAttuale,
                            oraFine
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            }

            @Override
            public void done() {
                try {
                    List<StopTime> stopTimes = get();
                    Map<String, String> orariFermata = new TreeMap<>();
                    for (StopTime st : stopTimes) {
                        if (st.getStopId().equals(stop.getStopId())) {
                            orariFermata.put(st.getTripId(), st.getArrivalTime());
                        }
                    }

                    List<Trip> trips = mappa.getTrips();
                    List<Route> routes = mappa.getLinee();
                    Map<String, String> mappaLinee = new HashMap<>();
                    for (Trip t : trips) {
                        for (Route r : routes) {
                            if (r.getRouteId().equals(t.getRouteId())) {
                                mappaLinee.put(t.getTripId(), r.getRouteShortName());
                                break;
                            }
                        }
                    }

                    model.setRowCount(0);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime oraAttuale = LocalTime.now();
                    Set<String> righeUniche = new HashSet<>();
                    List<Object[]> arriviValidi = new ArrayList<>();

                    // Filtra gli arrivi entro 40 minuti
                    for (Map.Entry<String, String> entry : orariFermata.entrySet()) {
                        try {
                            LocalTime arrivo = LocalTime.parse(entry.getValue(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                            if (!arrivo.isBefore(oraAttuale) && !arrivo.isAfter(oraAttuale.plusMinutes(40))) {
                                String tripId = entry.getKey();
                                String linea = mappaLinee.getOrDefault(tripId, "—");
                                String chiave = linea + "@" + arrivo.format(formatter);
                                if (!righeUniche.contains(chiave)) {
                                    righeUniche.add(chiave);
                                    arriviValidi.add(new Object[]{linea, arrivo.format(formatter)});
                                }
                            }
                        } catch (Exception ignored) {}
                    }

                    // Ordina per orario
                    arriviValidi.sort(Comparator.comparing(o -> LocalTime.parse((String) o[1], formatter)));
                    for (Object[] riga : arriviValidi) {
                        model.addRow(riga);
                    }

                    if (model.getRowCount() == 0) {
                        model.addRow(new Object[]{"—", "Nessun arrivo entro 40 minuti"});
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }


    /**
     * Aggiorna gli orari della fermata in tempo reale.
     * <p>
     * Se non ci sono dati realtime o non si è online, esegue il fallback agli orari offline.
     * </p>
     */
    private void aggiornaOrariRealtime() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        model.addRow(new Object[]{"Caricamento...", ""});

        if (!GestoreMode.isOnline()) {
            caricaOrariFermata();
            return;
        }

        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                GtfsRealtime.FeedMessage tripUpdates = null;
                long start = System.currentTimeMillis();
                // Attende feed realtime o timeout
                while ((tripUpdates == null || tripUpdates.getEntityCount() == 0)
                        && System.currentTimeMillis() - start < 5000) {
                    tripUpdates = GestoreRealTime.getLatestTripUpdates();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {}
                }

                if (tripUpdates == null || tripUpdates.getEntityCount() == 0)
                    return Collections.emptyList();

                List<Trip> trips = mappa.getTrips();
                List<Route> routes = mappa.getLinee();
                Map<String, String> mappaLinee = new HashMap<>();
                for (Trip t : trips) {
                    for (Route r : routes) {
                        if (r.getRouteId().equals(t.getRouteId())) {
                            mappaLinee.put(t.getTripId(), r.getRouteShortName());
                            break;
                        }
                    }
                }

                Set<String> righeUniche = new HashSet<>();
                List<Object[]> arrivi = new ArrayList<>();
                LocalTime oraAttuale = LocalTime.now();
                LocalTime oraFine = oraAttuale.plusMinutes(40);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                // Filtra e aggiungi arrivi validi
                for (GtfsRealtime.FeedEntity entity : tripUpdates.getEntityList()) {
                    if (!entity.hasTripUpdate()) continue;

                    for (GtfsRealtime.TripUpdate.StopTimeUpdate stu : entity.getTripUpdate().getStopTimeUpdateList()) {
                        if (stu.getStopId().equals(stop.getStopId()) && stu.hasArrival()) {
                            long epochSec = stu.getArrival().getTime();
                            LocalTime arrivo = Instant.ofEpochSecond(epochSec)
                                    .atZone(ZoneId.of("Europe/Rome"))
                                    .toLocalTime();

                            if (!arrivo.isBefore(oraAttuale) && !arrivo.isAfter(oraFine)) {
                                String tripId = entity.getTripUpdate().getTrip().getTripId();
                                String linea = mappaLinee.get(tripId);
                                if (linea == null || linea.equals("—")) continue;

                                String chiave = linea + "@" + arrivo.format(formatter);
                                if (righeUniche.add(chiave)) {
                                    arrivi.add(new Object[]{linea, arrivo.format(formatter)});
                                }
                            }
                        }
                    }
                }

                // Ordina per orario
                arrivi.sort(Comparator.comparing(o -> LocalTime.parse((String) o[1], formatter)));
                return arrivi;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> arrivi = get();
                    model.setRowCount(0);

                    if (arrivi.isEmpty()) {
                        caricaOrariFermata();
                    } else {
                        for (Object[] riga : arrivi) {
                            model.addRow(riga);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    caricaOrariFermata();
                }
            }
        }.execute();
    }
}
