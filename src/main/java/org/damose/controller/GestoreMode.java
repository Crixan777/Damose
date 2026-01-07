package org.damose.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * GestoreMode monitora costantemente lo stato della connessione di rete.
 *
 * <p>
 * Funzionalità principali:
 * <ul>
 *     <li>Verifica periodicamente se l'applicazione è online o offline</li>
 *     <li>Notifica i listener registrati quando lo stato cambia</li>
 *     <li>Avvia o ferma il feed in tempo reale tramite {@link GestoreRealTime}</li>
 * </ul>
 * </p>
 *
 *
 */
public class GestoreMode extends Thread {

    /** Stato corrente della connessione: true = online, false = offline */
    private static boolean isOnline = true;

    /** Stato precedente per rilevare i cambiamenti */
    private static boolean lastState = true;

    /** Lista dei listener da notificare quando cambia lo stato */
    private static final List<Runnable> statusListeners = new ArrayList<>();

    /**
     * Registra un listener da eseguire ogni volta che lo stato della connessione cambia.
     *
     * @param listener Runnable da eseguire al cambio di stato
     */
    public static void addStatusListener(Runnable listener) {
        statusListeners.add(listener);
    }

    /**
     * Notifica tutti i listener registrati del cambio di stato.
     * Viene chiamato solo quando il valore di {@link #isOnline} cambia.
     */
    private static void notifyStatusChange() {
        for (Runnable r : statusListeners) {
            r.run();
        }
    }

    /**
     * Ciclo principale del thread.
     * Controlla la connessione ogni 2 secondi e aggiorna lo stato.
     * Se lo stato cambia, vengono notificati tutti i listener.
     */
    @Override
    public void run() {
        while (true) {
            boolean nuovoStato = controllaConnessione(); // verifica connessione

            // se lo stato è cambiato rispetto all'ultimo controllo
            if (nuovoStato != lastState) {
                lastState = nuovoStato;
                isOnline = nuovoStato;
                notifyStatusChange(); // notifica listener

                // avvia o ferma aggiornamento realtime
                if (isOnline) {
                    GestoreRealTime.startAggiornamento();
                } else {
                    GestoreRealTime.stopAggiornamento();
                }
            }

            // attende 5 secondi prima del prossimo controllo
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // se il thread viene interrotto, ripristina lo stato di interruzione
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Controlla se la connessione Internet è attiva.
     * Tenta di aprire un flusso verso Google.
     *
     * @return true se online, false altrimenti
     */
    private boolean controllaConnessione() {
        try {
            URL url = new URL("https://www.google.com");
            try (InputStream in = url.openStream()) { // prova ad aprire il flusso
                return true; // connessione OK
            }
        } catch (IOException e) {
            return false; // connessione fallita
        }
    }

    /**
     * Restituisce lo stato corrente della connessione.
     *
     * @return true se online, false se offline
     */
    public static boolean isOnline() {
        return isOnline;
    }
}
