package org.damose.view;

import org.damose.controller.GestorePannelloPreferiti;

import javax.swing.*;
import java.awt.*;

public class PannelloPreferiti extends JPanel {
    private final JTextField campoRicerca;           // Campo per filtrare la lista
    private final DefaultListModel<String> modelloLista; // Modello della lista
    private final JList<String> listaPreferiti;      // Lista di fermate e linee preferite
    private final JButton chiudiButton;              // Bottone per chiudere il pannello

    public PannelloPreferiti(MyFrame frame, MappaAutobus mappa) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Pannello superiore con titolo e barra ricerca
        JPanel pannelloSopra = new JPanel(new BorderLayout());
        pannelloSopra.setBackground(new Color(245, 245, 245));

        JLabel titolo = new JLabel("<html><h2 style='color:#8B0000;'>Fermate e linee preferite </h2></html>");
        campoRicerca = new JTextField();
        campoRicerca.setFont(new Font("Arial", Font.PLAIN, 18));

        // Bottone X per chiudere
        ImageIcon iconaChiudi = new ImageIcon("src/main/resources/img/chiudi.png");
        iconaChiudi = new ImageIcon(iconaChiudi.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        chiudiButton = new JButton(iconaChiudi);
        chiudiButton.setBorderPainted(false);
        chiudiButton.setContentAreaFilled(false);
        chiudiButton.setFocusPainted(false);
        chiudiButton.setOpaque(false);
        chiudiButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pannelloSopra.add(chiudiButton, BorderLayout.EAST);

        pannelloSopra.add(titolo, BorderLayout.CENTER);
        pannelloSopra.add(campoRicerca, BorderLayout.SOUTH);
        add(pannelloSopra, BorderLayout.NORTH);

        // Lista preferiti
        modelloLista = new DefaultListModel<>();
        listaPreferiti = new JList<>(modelloLista);
        listaPreferiti.setFont(new Font("Arial", Font.PLAIN, 17));

        // Renderer personalizzato per distinguere fermate e linee
        listaPreferiti.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                String testo = value.toString().toLowerCase();

                if (testo.startsWith("fermata:")) {
                    label.setForeground(new Color(139, 0, 0));
                } else if (testo.startsWith("linea:")) {
                    label.setForeground(new Color(0, 0, 139));
                } else {
                    label.setForeground(Color.BLACK);
                }

                label.setBackground(isSelected ? new Color(230, 230, 250) : Color.WHITE);
                label.setOpaque(true);

                return label;
            }
        });

        add(new JScrollPane(listaPreferiti), BorderLayout.CENTER);

        // Collega il gestore del pannello
        new GestorePannelloPreferiti(this, frame, mappa);
    }

    // Metodi per il controller
    public JTextField getCampoRicerca() { return campoRicerca; }
    public DefaultListModel<String> getModelloLista() { return modelloLista; }
    public JList<String> getListaPreferiti() { return listaPreferiti; }
    public JButton getChiudiButton() { return chiudiButton; }
}
