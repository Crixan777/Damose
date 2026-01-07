package org.damose.controller;

import org.damose.view.MappaAutobus;
import org.damose.view.MyFrame;
import org.damose.view.PannelloImpostazioni;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * GestorePannelloImpostazioni gestisce le funzionalità del pannello Impostazioni.
 *
 * <p>Responsabilità principali:
 * <ul>
 *     <li>Aggiorna in tempo reale lo stato Online/Offline tramite {@link GestoreMode}</li>
 *     <li>Gestisce la chiusura del pannello laterale dell'applicazione</li>
 * </ul>
 * </p>
 *
 *
 */
public class GestorePannelloImpostazioni {

    /** Pannello impostazioni da gestire */
    private final PannelloImpostazioni view;

    /** Frame principale dell'applicazione */
    private final MyFrame frame;

    /**
     * Costruisce un gestore per il pannello Impostazioni.
     * Registra i listener e aggiorna lo stato iniziale.
     *
     * @param view pannello impostazioni
     * @param mappa mappa autobus (non utilizzata direttamente)
     * @param frame frame principale
     */
    public GestorePannelloImpostazioni(PannelloImpostazioni view, MappaAutobus mappa, MyFrame frame) {
        this.view = view;
        this.frame = frame;

        // registra listener pulsante chiudi
        registraListener();

        // aggiorna subito lo stato Online/Offline
        aggiornaStato();

        // registra listener per aggiornamenti continui quando cambia lo stato della connessione
        GestoreMode.addStatusListener(this::aggiornaStato);
    }

    /**
     * Aggiorna la label dello stato Online/Offline.
     * Modifica testo, colore del testo e sfondo in base allo stato reale.
     */
    private void aggiornaStato() {
        boolean online = GestoreMode.isOnline();
        JLabel lbl = view.getLblStatusValue();

        // aggiorna UI sul thread Swing
        SwingUtilities.invokeLater(() -> {
            if (online) {
                lbl.setText("Online");
                lbl.setForeground(new Color(0, 128, 0));       // verde testo
                lbl.setBackground(new Color(220, 255, 220));   // verde chiaro sfondo
            } else {
                lbl.setText("Offline");
                lbl.setForeground(Color.DARK_GRAY);           // grigio testo
                lbl.setBackground(new Color(245, 245, 245));   // grigio chiaro sfondo
            }
        });
    }

    /** Registra il listener per il pulsante chiudi */
    private void registraListener() {
        view.getChiudiButton().addActionListener(e -> chiudiPannello());
    }

    /**
     * Chiude il pannello laterale.
     * <ul>
     *     <li>Riduce dimensione a zero</li>
     *     <li>Rimuove tutti i componenti interni</li>
     *     <li>Resetta i campi privati del frame tramite reflection</li>
     * </ul>
     */
    private void chiudiPannello() {
        // ottiene il pannello laterale (BorderLayout.WEST)
        JPanel pannelloLaterale = (JPanel) ((BorderLayout) frame.getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.WEST);

        if (pannelloLaterale != null) {
            pannelloLaterale.setPreferredSize(new Dimension(0, frame.getHeight())); // nasconde il pannello
            pannelloLaterale.removeAll();   // rimuove componenti
            pannelloLaterale.revalidate();  // aggiorna layout
            pannelloLaterale.repaint();     // ridisegna
        }

        // reset campi privati di MyFrame tramite reflection
        try {
            Field fieldVisibile = MyFrame.class.getDeclaredField("pannelloVisibile");
            fieldVisibile.setAccessible(true);
            fieldVisibile.setBoolean(frame, false);

            Field fieldUltimo = MyFrame.class.getDeclaredField("ultimoElementoPremuto");
            fieldUltimo.setAccessible(true);
            fieldUltimo.set(frame, null);
        } catch (Exception ignored) {}

        // aggiorna contenitore principale
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
    }
}
