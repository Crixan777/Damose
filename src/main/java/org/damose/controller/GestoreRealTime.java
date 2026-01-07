package org.damose.controller;

import com.google.transit.realtime.GtfsRealtime;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * GestoreRealTime:
 *
 * <p>Si occupa di scaricare e aggiornare i feed GTFS Realtime di Roma Mobilità
 * ogni 30 secondi. Fornisce posizioni dei veicoli e aggiornamenti dei trip
 * in tempo reale.</p>
 *
 * <p>Le operazioni principali sono:</p>
 * <ul>
 *   <li>startAggiornamento(): avvia il fetch automatico dei feed</li>
 *   <li>stopAggiornamento(): ferma il fetch automatico</li>
 *   <li>fetchRealtimeFeeds(): scarica manualmente i feed</li>
 *   <li>getLatestVehiclePositions()/getLatestTripUpdates(): restituiscono i dati più recenti</li>
 * </ul>
 */
public class GestoreRealTime {

    /** URL dei feed GTFS Realtime */
    public static final String VEHICLE_POSITIONS_URL =
            "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";
    public static final String TRIP_UPDATES_URL =
            "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";

    /** Ultimi feed scaricati */
    private static GtfsRealtime.FeedMessage latestVehiclePositions;
    private static GtfsRealtime.FeedMessage latestTripUpdates;

    /** Timer per aggiornamento periodico */
    private static Timer timer;

    /**
     * Avvia l'aggiornamento automatico dei feed ogni 30 secondi.
     * Se il programma è offline, non effettua il fetch.
     */
    public static synchronized void startAggiornamento() {
        stopAggiornamento(); // evita timer duplicati

        timer = new Timer("GTFSRealtimeUpdater", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (GestoreMode.isOnline()) {
                    fetchRealtimeFeeds();
                }
            }
        }, 0, 30_000);
    }

    /**
     * Ferma l'aggiornamento automatico dei feed.
     */
    public static synchronized void stopAggiornamento() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Scarica i feed GTFS Realtime di VehiclePositions e TripUpdates.
     * Aggiorna le variabili statiche con i dati più recenti.
     */
    public static void fetchRealtimeFeeds() {
        try (InputStream in = new URL(VEHICLE_POSITIONS_URL).openStream()) {
            latestVehiclePositions = GtfsRealtime.FeedMessage.parseFrom(in);
        } catch (Exception e) {
            latestVehiclePositions = null;
        }

        try (InputStream in = new URL(TRIP_UPDATES_URL).openStream()) {
            latestTripUpdates = GtfsRealtime.FeedMessage.parseFrom(in);
        } catch (Exception e) {
            latestTripUpdates = null;
        }
    }

    /**
     * Restituisce l'ultimo feed VehiclePositions disponibile.
     *
     * @return feed VehiclePositions o null se non disponibile
     */
    public static GtfsRealtime.FeedMessage getLatestVehiclePositions() {
        return latestVehiclePositions;
    }

    /**
     * Restituisce l'ultimo feed TripUpdates disponibile.
     *
     * @return feed TripUpdates o null se non disponibile
     */
    public static GtfsRealtime.FeedMessage getLatestTripUpdates() {
        return latestTripUpdates;
    }
}
