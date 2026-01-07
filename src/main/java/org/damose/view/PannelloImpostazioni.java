package org.damose.view;

import org.damose.controller.GestorePannelloImpostazioni;

import javax.swing.*;
import java.awt.*;

/**
 * Pannello delle impostazioni:
 * Mostra lo stato del programma (online/offline)
 * e consente di chiudere il pannello laterale.
 */
public class PannelloImpostazioni extends JPanel {

    private final JLabel lblStatusValue;   // Label che mostra online/offline
    private final JButton chiudiButton;    // Bottone per chiudere il pannello

    public PannelloImpostazioni(MyFrame frame, MappaAutobus mappa) {
        super(new BorderLayout());
        setBackground(Color.WHITE);

        // Header con titolo e bottone chiudi
        JPanel pannelloSopra = new JPanel(new BorderLayout());
        pannelloSopra.setBackground(new Color(245, 245, 245));

        JLabel titolo = new JLabel("<html><h2 style='color:#8B0000;'>Impostazioni</h2></html>");

        ImageIcon iconaChiudi = new ImageIcon("src/main/resources/img/chiudi.png");
        iconaChiudi = new ImageIcon(iconaChiudi.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        chiudiButton = new JButton(iconaChiudi);
        chiudiButton.setBorderPainted(false);
        chiudiButton.setContentAreaFilled(false);
        chiudiButton.setFocusPainted(false);
        chiudiButton.setOpaque(false);
        chiudiButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pannelloSopra.add(titolo, BorderLayout.CENTER);
        pannelloSopra.add(chiudiButton, BorderLayout.EAST);
        add(pannelloSopra, BorderLayout.NORTH);

        // Corpo centrale con status del programma
        JPanel centro = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        centro.setBackground(Color.WHITE);

        JLabel lblStatus = new JLabel("Status del programma:");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 16));
        lblStatus.setForeground(new Color(80, 80, 80));

        lblStatusValue = new JLabel("Offline", SwingConstants.CENTER);
        lblStatusValue.setFont(new Font("Arial", Font.PLAIN, 14));
        lblStatusValue.setOpaque(true);
        lblStatusValue.setForeground(Color.DARK_GRAY);
        lblStatusValue.setBackground(new Color(245, 245, 245));
        lblStatusValue.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        lblStatusValue.setPreferredSize(new Dimension(100, 30));

        centro.add(lblStatus);
        centro.add(lblStatusValue);

        add(centro, BorderLayout.CENTER);

        // Avvia gestore per il pannello impostazioni
        new GestorePannelloImpostazioni(this, mappa, frame);
    }

    public JLabel getLblStatusValue() { return lblStatusValue; }
    public JButton getChiudiButton() { return chiudiButton; }
}
