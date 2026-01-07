package org.damose.view;

import org.damose.controller.GestorePannelloRicerca;

import javax.swing.*;
import java.awt.*;

public class PannelloRicerca extends JPanel {

    private final JTextField campoRicerca;         // Campo per inserire la ricerca
    private final DefaultListModel<String> modelloLista; // Modello della lista dei risultati
    private final JList<String> listaRisultati;    // Lista dei risultati della ricerca
    private final JButton chiudiButton;            // Bottone per chiudere il pannello

    public PannelloRicerca(MyFrame frame, MappaAutobus mappa) {
        super(new BorderLayout());
        setBackground(Color.WHITE);

        // Pannello superiore con titolo e barra ricerca
        JPanel pannelloSopra = new JPanel(new BorderLayout());
        pannelloSopra.setBackground(new Color(245, 245, 245));

        JLabel titolo = new JLabel("<html><h2 style='color:#8B0000;'>Ricerca fermate e linee</h2></html>");
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

        // Lista dei risultati
        modelloLista = new DefaultListModel<>();
        listaRisultati = new JList<>(modelloLista);
        listaRisultati.setFont(new Font("Arial", Font.PLAIN, 17));

        // Renderer per distinguere fermate e linee
        listaRisultati.setCellRenderer(new DefaultListCellRenderer() {
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

        add(new JScrollPane(listaRisultati), BorderLayout.CENTER);

        // Collega il gestore del pannello
        new GestorePannelloRicerca(this, frame, mappa);
    }

    // Metodi per il controller
    public JTextField getCampoRicerca() { return campoRicerca; }
    public DefaultListModel<String> getModelloLista() { return modelloLista; }
    public JList<String> getListaRisultati() { return listaRisultati; }
    public JButton getChiudiButton() { return chiudiButton; }
}
