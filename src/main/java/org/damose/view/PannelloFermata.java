package org.damose.view;

import org.damose.model.Stop;
import org.damose.controller.GestorePannelloPreferiti;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Pannello laterale che mostra le informazioni di una fermata.
 * Include:
 * - Titolo con nome fermata
 * - Pulsante per chiudere il pannello
 * - Checkbox "stella" per aggiungere/rimuovere la fermata dai preferiti
 * - Informazioni base (ID fermata)
 * - Tabella con orari delle linee in arrivo
 */
public class PannelloFermata extends JPanel {

    /** Fermata visualizzata nel pannello */
    private final Stop stop;

    /** Checkbox per indicare fermata preferita */
    private final JCheckBox stellaCheckBox;

    /** Bottone per chiudere il pannello */
    private final JButton chiudiButton;

    /** Tabella con gli orari dei bus */
    private final JTable tabellaOrari;

    /** Modello della tabella */
    private final DefaultTableModel tableModel;

    /**
     * Costruttore.
     * Inizializza layout, elementi grafici e tabella degli orari.
     *
     * @param stop fermata da visualizzare
     */
    public PannelloFermata(Stop stop) {
        this.stop = stop;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // panello superiore
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // Titolo con nome fermata
        JLabel titolo = new JLabel(stop.getStopName());
        titolo.setFont(new Font("Arial", Font.BOLD, 18));
        titolo.setForeground(new Color(139, 0, 0)); // rosso scuro
        topPanel.add(titolo, BorderLayout.WEST);

        // Pannello destro con checkbox stella + bottone chiudi
        JPanel destraPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        destraPanel.setBackground(Color.WHITE);

        // Checkbox "stella" per preferiti
        stellaCheckBox = new JCheckBox();
        stellaCheckBox.setBackground(Color.WHITE);

        ImageIcon stellaVuota = new ImageIcon("src/main/resources/img/stella_vuota.png");
        ImageIcon stellaPiena = new ImageIcon("src/main/resources/img/stella_piena.png");
        stellaVuota = new ImageIcon(stellaVuota.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        stellaPiena = new ImageIcon(stellaPiena.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        stellaCheckBox.setIcon(stellaVuota);
        stellaCheckBox.setSelectedIcon(stellaPiena);
        stellaCheckBox.setSelected(GestorePannelloPreferiti.isPreferito(stop));
        destraPanel.add(stellaCheckBox);

        // Bottone chiudi pannello
        ImageIcon iconaChiudi = new ImageIcon("src/main/resources/img/chiudi.png");
        iconaChiudi = new ImageIcon(iconaChiudi.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        chiudiButton = new JButton(iconaChiudi);
        chiudiButton.setBorderPainted(false);
        chiudiButton.setContentAreaFilled(false);
        chiudiButton.setFocusPainted(false);
        chiudiButton.setOpaque(false);
        chiudiButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        destraPanel.add(chiudiButton);

        topPanel.add(destraPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        //info fermata
        JTextArea info = new JTextArea();
        info.setText("ID: " + stop.getStopId());
        info.setEditable(false);
        info.setBackground(Color.WHITE);
        info.setFont(new Font("Arial", Font.PLAIN, 15));

        //tabella orari
        String[] colonne = {"Linea", "Orario arrivo"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // celle non modificabili
            }
        };

        tabellaOrari = new JTable(tableModel);
        tabellaOrari.setFillsViewportHeight(true);
        tabellaOrari.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaOrari.setForeground(new Color(0, 0, 139)); // blu scuro

        // Header tabella personalizzato
        JTableHeader header = tabellaOrari.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 15));
        header.setForeground(new Color(139, 0, 0)); // rosso scuro
        header.setBackground(new Color(245, 245, 245)); // grigio chiaro elegante

        // Pannello centrale con info + tabella
        JPanel centroPanel = new JPanel(new BorderLayout());
        centroPanel.setBackground(Color.WHITE);
        centroPanel.add(info, BorderLayout.NORTH);
        centroPanel.add(new JScrollPane(tabellaOrari), BorderLayout.CENTER);

        add(centroPanel, BorderLayout.CENTER);
    }

    // getters

    public Stop getStop() { return stop; }
    public JCheckBox getStellaCheckBox() { return stellaCheckBox; }
    public JButton getChiudiButton() { return chiudiButton; }
    public JTable getTabellaOrari() { return tabellaOrari; }
    public DefaultTableModel getTableModel() { return tableModel; }
}
