package org.damose.controller;

import org.damose.view.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GestoreFrame si occupa di gestire le azioni dei pulsanti principali della GUI:
 * ricerca, preferiti, impostazioni e utente.
 *
 * <p>
 * Quando un pulsante viene cliccato:
 * <ul>
 *     <li>Aggiorna il pannello laterale di {@link MyFrame}</li>
 *     <li>Resetta la vista della mappa tramite {@link GestoreMappa}</li>
 * </ul>
 * </p>
 *
 *
 */
public class GestoreFrame implements ActionListener {

    /** Riferimento al frame principale */
    private final MyFrame frame;

    /** Mappa degli autobus da mostrare nella GUI */
    private final MappaAutobus mappa;

    /** Pulsante per la ricerca */
    private final JButton ricerca;

    /** Pulsante per i preferiti */
    private final JButton preferiti;

    /** Pulsante per le impostazioni */
    private final JButton settings;

    /** Pulsante per l'utente */
    private final JButton utente;

    /**
     * Costruisce un nuovo GestoreFrame associato al frame e ai pulsanti principali.
     *
     * @param frame frame principale dell'applicazione
     * @param mappa mappa degli autobus
     * @param ricerca pulsante di ricerca
     * @param preferiti pulsante dei preferiti
     * @param settings pulsante delle impostazioni
     * @param utente pulsante utente
     */
    public GestoreFrame(MyFrame frame, MappaAutobus mappa, JButton ricerca, JButton preferiti, JButton settings, JButton utente) {
        this.frame = frame;
        this.mappa = mappa;
        this.ricerca = ricerca;
        this.preferiti = preferiti;
        this.settings = settings;
        this.utente = utente;
    }

    /**
     * Registra questo gestore come listener per tutti i pulsanti principali.
     * Dopo la registrazione, tutti i click sui pulsanti saranno gestiti
     * dal metodo {@link #actionPerformed(ActionEvent)}.
     */
    public void registraAzioni() {
        ricerca.addActionListener(this);
        preferiti.addActionListener(this);
        settings.addActionListener(this);
        utente.addActionListener(this);
    }

    /**
     * Gestisce l'evento di click sui pulsanti principali.
     * Aggiorna il pannello laterale del frame e resetta la vista della mappa.
     *
     * @param e evento generato dal click sul pulsante
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource(); // Identifica quale pulsante è stato cliccato

        // Se il pulsante cliccato è "Ricerca"
        if (source == ricerca) {
            // Aggiorna il pannello laterale con il pannello di ricerca
            frame.aggiornaPannello(new PannelloRicerca(frame, mappa), (JButton) source);
            // Reset della vista della mappa a stato iniziale
            GestoreMappa.resetVistaMappa();
        }

        // Se il pulsante cliccato è "Preferiti"
        if (source == preferiti) {
            // Aggiorna il pannello laterale con il pannello dei preferiti
            frame.aggiornaPannello(new PannelloPreferiti(frame, mappa), (JButton) source);
            GestoreMappa.resetVistaMappa();
        }

        // Se il pulsante cliccato è "Settings"
        if (source == settings) {
            // Aggiorna il pannello laterale con il pannello delle impostazioni
            frame.aggiornaPannello(new PannelloImpostazioni(frame, mappa), (JButton) source);
            GestoreMappa.resetVistaMappa();
        }

        // Se il pulsante cliccato è "Utente"
        if (source == utente) {
            // Aggiorna il pannello laterale con il pannello dell'utente
            frame.aggiornaPannello(new PannelloUtente(frame, mappa), (JButton) source);
            GestoreMappa.resetVistaMappa();
        }
    }
}
