package org.damose.controller;

import org.damose.model.*;
import org.damose.view.MappaAutobus;
import org.damose.view.MyFrame;
import org.damose.view.PannelloLinea;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gestore del pannello Linea.
 * Gestisce:
 *   - caricamento fermate della linea
 *   - aggiornamento in tempo reale dei bus su quella linea
 *   - gestione pulsanti (chiudi, stella preferiti)
 */
public class GestorePannelloLinea {

    private final PannelloLinea view;
    private final Route route;
    private final MyFrame frame;
    private Timer timerBus;
    private boolean attivo = false;

    /**
     * Costruttore: inizializza il gestore e registra listener.
     * @param view il pannello linea
     * @param route la linea da visualizzare
     * @param frame il frame principale
     */
    public GestorePannelloLinea(PannelloLinea view, Route route, MyFrame frame) {
        this.view = view;
        this.route = route;
        this.frame = frame;

        registraListener();        // registra listener pulsanti e tabella
        caricaFermateLinea();      // carica fermate principali della linea

        if (GestoreMode.isOnline()) {
            avviaAggiornamentoBus(); // avvia aggiornamento realtime se online
        }

        // aggiorna automaticamente quando cambia lo stato della rete
        GestoreMode.addStatusListener(() -> {
            if (GestoreMode.isOnline()) {
                avviaAggiornamentoBus();
            } else {
                fermaAggiornamentoBus();
            }
        });
    }

    /** Avvia il timer per aggiornare in realtime la posizione dei bus */
    private void avviaAggiornamentoBus() {
        if (timerBus != null) timerBus.cancel();
        attivo = true;

        timerBus = new Timer("AggiornaBusRealtime", true);
        timerBus.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!attivo || !GestoreMode.isOnline()) return;
                SwingUtilities.invokeLater(() -> aggiornaBusRealtime());
            }
        }, 0, 30_000); // ogni 30 secondi
    }

    /** Ferma l'aggiornamento realtime dei bus */
    private void fermaAggiornamentoBus() {
        attivo = false;
        if (timerBus != null) {
            timerBus.cancel();
            timerBus = null;
        }
        GestoreMappa.clearSoloPercorso(); // rimuove bus dalla mappa, mantiene waypoint
    }

    /** Aggiorna la posizione dei bus sulla mappa in realtime */
    private void aggiornaBusRealtime() {
        final MappaAutobus mappa = view.mappa;
        if (mappa == null) return;

        new SwingWorker<List<BusWaypoint>, Void>() {
            @Override
            public List<BusWaypoint> doInBackground() {
                List<BusWaypoint> busLinea = new ArrayList<>();

                try {
                    com.google.transit.realtime.GtfsRealtime.FeedMessage vehiclePositions =
                            GestoreRealTime.getLatestVehiclePositions();
                    if (vehiclePositions == null) return busLinea;

                    List<Trip> trips = mappa.getTrips();

                    for (com.google.transit.realtime.GtfsRealtime.FeedEntity entity : vehiclePositions.getEntityList()) {
                        if (!entity.hasVehicle()) continue;
                        com.google.transit.realtime.GtfsRealtime.VehiclePosition vp = entity.getVehicle();
                        if (!vp.hasTrip() || !vp.hasPosition()) continue;

                        String routeId = vp.getTrip().getRouteId();
                        if (!route.getRouteId().equals(routeId)) continue;

                        // crea waypoint del bus
                        busLinea.add(new BusWaypoint(vp.getPosition().getLatitude(),
                                vp.getPosition().getLongitude(),
                                null)); // stop=null perché non serve
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return busLinea;
            }

            @Override
            public void done() {
                if (!attivo) return;
                try {
                    List<BusWaypoint> busLinea = get();
                    GestoreMappa.mostraBusSuMappa(busLinea); // aggiorna mappa
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /** Carica la lista delle fermate della linea e popola la tabella */
    private void caricaFermateLinea() {
        final javax.swing.table.DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        model.addRow(new Object[]{"Caricamento...", " "});

        new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                MappaAutobus mappa = view.mappa;
                List<Trip> trips = mappa.getTrips();
                List<Stop> fermate = mappa.getFermate();
                List<StopTime> stopTimes = mappa.getStopTimes();

                // filtra trips della linea corrente
                List<Trip> tripsLinea = new ArrayList<>();
                for (Trip t : trips) if (t.getRouteId().equals(route.getRouteId())) tripsLinea.add(t);

                if (tripsLinea.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        model.addRow(new Object[]{"-", "Nessun trip trovato"});
                    });
                    return null;
                }

                Trip tripCompleto = null;
                List<StopTime> stopTimesTripCompleto = null;
                int maxFermate = 0;

                for (Trip t : tripsLinea) {
                    List<StopTime> stopTimesTrip = new ArrayList<>();
                    for (StopTime st : stopTimes) if (st.getTripId().equals(t.getTripId())) stopTimesTrip.add(st);
                    stopTimesTrip.sort(Comparator.comparingInt(StopTime::getStopSequence));
                    if (stopTimesTrip.size() > maxFermate) {
                        maxFermate = stopTimesTrip.size();
                        tripCompleto = t;
                        stopTimesTripCompleto = stopTimesTrip;
                    }
                }

                if (tripCompleto == null || stopTimesTripCompleto == null || stopTimesTripCompleto.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        model.addRow(new Object[]{"-", "Nessuna fermata disponibile"});
                    });
                    return null;
                }

                final List<StopTime> stopTimesFinal = stopTimesTripCompleto;
                SwingUtilities.invokeLater(() -> {
                    model.setRowCount(0);
                    AtomicInteger idx = new AtomicInteger(1);
                    for (StopTime st : stopTimesFinal) {
                        for (Stop f : fermate) {
                            if (f.getStopId().equals(st.getStopId())) {
                                model.addRow(new Object[]{idx.getAndIncrement(), f.getStopName()});
                                break;
                            }
                        }
                    }
                    if (model.getRowCount() == 0) model.addRow(new Object[]{"-", "Nessuna fermata disponibile"});
                });
                return null;
            }
        }.execute();
    }

    /** Registra listener pulsanti, checkbox preferiti e selezione tabella */
    private void registraListener() {
        // checkbox stella
        view.getStellaCheckBox().addActionListener(e -> {
            if (view.getStellaCheckBox().isSelected())
                GestorePannelloPreferiti.aggiungiPreferito(route);
            else
                GestorePannelloPreferiti.rimuoviPreferito(route);
        });

        // pulsante chiudi
        view.getChiudiButton().addActionListener(e -> chiudiPannello());

        // selezione tabella fermate: zoom sulla fermata selezionata
        view.getTabellaFermate().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getTabellaFermate().getSelectedRow();
                if (row >= 0) {
                    String nomeFermata = view.getTabellaFermate().getValueAt(row, 1).toString();
                    MappaAutobus mappa = view.mappa;
                    for (Stop stop : mappa.getFermate()) {
                        if (stop.getStopName().equalsIgnoreCase(nomeFermata)) {
                            GestoreMappa.zoomFermata(stop);
                            break;
                        }
                    }
                }
            }
        });
    }

    /** Chiude il pannello linea e resetta mappa */
    private void chiudiPannello() {
        fermaAggiornamentoBus();

        JPanel pannelloLaterale = (JPanel) frame.getContentPane().getComponent(1);
        pannelloLaterale.setPreferredSize(new Dimension(0, frame.getHeight()));
        pannelloLaterale.removeAll();
        pannelloLaterale.revalidate();
        pannelloLaterale.repaint();

        GestoreMappa.clearPainterLinea();
        GestoreMappa.mostraTuttiWaypoints();
        frame.repaint();

        try {
            Field visibile = MyFrame.class.getDeclaredField("pannelloVisibile");
            visibile.setAccessible(true);
            visibile.setBoolean(frame, false);
            Field ultimo = MyFrame.class.getDeclaredField("ultimoElementoPremuto");
            ultimo.setAccessible(true);
            ultimo.set(frame, null);
        } catch (Exception ignored) {}
    }
}
