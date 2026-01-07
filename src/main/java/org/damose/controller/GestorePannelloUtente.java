package org.damose.controller;

import org.damose.view.MappaAutobus;
import org.damose.view.PannelloUtente;
import org.damose.view.MyFrame;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * Gestore del pannello utente.
 *
 * <p>Si occupa di:</p>
 * <ul>
 *   <li>Registrare l'ActionListener sul bottone "Salva" (attualmente work in progress)</li>
 *   <li>Gestire la chiusura del pannello laterale</li>
 * </ul>
 */
public class GestorePannelloUtente {

    private final PannelloUtente view;
    private final MappaAutobus mappa;
    private final MyFrame frame;

    /**
     * Costruttore: inizializza il gestore e registra i listener.
     *
     * @param view  pannello utente
     * @param mappa mappa autobus
     * @param frame frame principale dell'applicazione
     */
    public GestorePannelloUtente(PannelloUtente view, MappaAutobus mappa, MyFrame frame) {
        this.view = view;
        this.mappa = mappa;
        this.frame = frame;

        registraListener();
    }

    /**
     * Registra i listener per i bottoni "Salva" e "Chiudi".
     */
    private void registraListener() {
        // Listener per il bottone Salva / Registra
        view.getBtnSalva().addActionListener(e -> {
            // Work in progress: qui si può salvare nome, email, password in un model o file
            // Esempio: salva su file, database o aggiorna il modello utente
        });

        // Listener per il bottone Chiudi
        view.getChiudiButton().addActionListener(e -> chiudiPannello());
    }

    /**
     * Chiude il pannello laterale e resetta lo stato interno del frame.
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
