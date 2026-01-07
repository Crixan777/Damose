package org.damose.view;

import org.damose.controller.GestorePannelloUtente;

import javax.swing.*;
import java.awt.*;

public class PannelloUtente extends JPanel {

    public final JTextField tfNome;         // Campo per inserire il nome
    public final JTextField tfEmail;        // Campo per inserire l'email
    public final JPasswordField pfPassword; // Campo per inserire la password
    public final JButton btnSalva;          // Bottone per registrare l'utente
    private final JButton chiudiButton;     // Bottone per chiudere il pannello

    public PannelloUtente(MyFrame frame, MappaAutobus mappa) {
        super(new BorderLayout());
        setBackground(Color.WHITE);

        // Pannello superiore con titolo e bottone X
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel titolo = new JLabel("<html><h2 style='color:#8B0000;'>Registrazione Utente</h2></html>");
        topPanel.add(titolo, BorderLayout.WEST);

        // Bottone X per chiudere
        ImageIcon iconaChiudi = new ImageIcon("src/main/resources/img/chiudi.png");
        iconaChiudi = new ImageIcon(iconaChiudi.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        chiudiButton = new JButton(iconaChiudi);
        chiudiButton.setBorderPainted(false);
        chiudiButton.setContentAreaFilled(false);
        chiudiButton.setFocusPainted(false);
        chiudiButton.setOpaque(false);
        chiudiButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        topPanel.add(chiudiButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Pannello centrale con campi nome, email e password
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centro.setBackground(Color.WHITE);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setForeground(new Color(139, 0, 0));
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        tfNome = new JTextField();
        tfNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(new Color(139, 0, 0));
        lblEmail.setFont(new Font("Arial", Font.BOLD, 15));
        tfEmail = new JTextField();
        tfEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(new Color(139, 0, 0));
        lblPassword.setFont(new Font("Arial", Font.BOLD, 15));
        pfPassword = new JPasswordField();
        pfPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        centro.add(lblNome);
        centro.add(tfNome);
        centro.add(Box.createRigidArea(new Dimension(0, 10)));
        centro.add(lblEmail);
        centro.add(tfEmail);
        centro.add(Box.createRigidArea(new Dimension(0, 10)));
        centro.add(lblPassword);
        centro.add(pfPassword);

        add(centro, BorderLayout.CENTER);

        // Pannello inferiore con bottone salva
        btnSalva = new JButton("Registra");
        btnSalva.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalva.setForeground(Color.WHITE);
        btnSalva.setBackground(new Color(139, 0, 0));
        btnSalva.setFocusPainted(false);
        btnSalva.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnSalva.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.setBackground(Color.WHITE);
        bot.add(btnSalva);
        add(bot, BorderLayout.SOUTH);

        // Collega il gestore del pannello
        new GestorePannelloUtente(this, mappa, frame);
    }

    // Getter per il controller
    public JTextField getTfNome() { return tfNome; }
    public JTextField getTfEmail() { return tfEmail; }
    public JPasswordField getPfPassword() { return pfPassword; }
    public JButton getBtnSalva() { return btnSalva; }
    public JButton getChiudiButton() { return chiudiButton; }
}
