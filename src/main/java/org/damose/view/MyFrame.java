package org.damose.view;

import org.damose.controller.GestoreFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Frame principale dell'applicazione Damose.
 * Contiene:
 * - Barra superiore con pulsanti (Ricerca, Preferiti, Impostazioni, Utente)
 * - Pannello laterale per contenuti dinamici
 * - Pannello centrale con la mappa
 */
public class MyFrame extends JFrame {

    /** Pulsanti principali della barra superiore */
    private JButton ricerca;
    private JButton preferiti;
    private JButton settings;
    private JButton utente;

    /** Pannello laterale per visualizzare contenuti dinamici */
    private JPanel pannelloLaterale;

    /** Stato di visibilità del pannello laterale */
    private boolean pannelloVisibile = false;

    /** Ultimo elemento (bottone) premuto */
    private Object ultimoElementoPremuto = null;

    /** Colori personalizzati */
    private final Color ROSSO_BORDO = new Color(139, 0, 0);
    private final Color ROSSO_VIVO = new Color(200, 0, 0);
    private final Color SFONDO_CHIARO = new Color(245, 245, 245);

    /**
     * Costruttore.
     * Inizializza frame, pulsanti, pannello laterale e mappa.
     */
    public MyFrame() {

        // Impostazioni principali del frame
        this.setTitle("Damose");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setSize(1980, 1030);
        ImageIcon logo = new ImageIcon("src/main/resources/img/logo.png");
        this.setIconImage(logo.getImage());

        // Pannello superiore con pulsanti
        JPanel pannelloTop = new JPanel();
        pannelloTop.setBackground(ROSSO_BORDO);

        ricerca = new JButton("Ricerca");
        preferiti = new JButton("Preferiti");
        settings = new JButton("Impostazioni");
        utente = new JButton("Utente");

        // Font e dimensione dei pulsanti
        Font fontBottoni = new Font("Arial", Font.BOLD, 18);
        Dimension dimBottoni = new Dimension(150, 40);

        JButton[] buttons = {ricerca, preferiti, settings, utente};
        for (JButton btn : buttons) {
            btn.setFont(fontBottoni);
            btn.setPreferredSize(dimBottoni);
            btn.setBackground(ROSSO_VIVO);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            pannelloTop.add(btn);
        }

        this.add(pannelloTop, BorderLayout.NORTH);

        // Pannello laterale (inizialmente nascosto)
        pannelloLaterale = new JPanel(new BorderLayout());
        pannelloLaterale.setBackground(SFONDO_CHIARO);
        pannelloLaterale.setPreferredSize(new Dimension(0, this.getHeight()));
        this.add(pannelloLaterale, BorderLayout.WEST);

        // Pannello centrale: mappa autobus
        MappaAutobus mappa = new MappaAutobus(this);
        this.add(mappa, BorderLayout.CENTER);

        // Gestore del frame (registrazione azioni pulsanti)
        GestoreFrame gestoreFrame = new GestoreFrame(this, mappa, ricerca, preferiti, settings, utente);
        gestoreFrame.registraAzioni();

        // Mostra il frame
        this.setVisible(true);
    }

    /**
     * Aggiorna il contenuto del pannello laterale.
     * Se lo stesso elemento viene premuto due volte, chiude il pannello.
     *
     * @param contenuto        contenuto da visualizzare nel pannello laterale
     * @param elementoPremuto  bottone o elemento premuto per determinare visibilità
     */
    public void aggiornaPannello(JComponent contenuto, Object elementoPremuto) {
        if (elementoPremuto == ultimoElementoPremuto && pannelloVisibile) {
            // Chiude il pannello laterale
            pannelloLaterale.setPreferredSize(new Dimension(0, this.getHeight()));
            pannelloLaterale.removeAll();
            pannelloVisibile = false;
            ultimoElementoPremuto = null;
        } else {
            // Apre o aggiorna il pannello laterale
            pannelloLaterale.removeAll();
            pannelloLaterale.add(contenuto, BorderLayout.CENTER);
            pannelloLaterale.setPreferredSize(new Dimension(400, this.getHeight()));
            pannelloVisibile = true;
            ultimoElementoPremuto = elementoPremuto;
        }

        pannelloLaterale.revalidate();
        pannelloLaterale.repaint();
    }
}
