package org.damose.view;

import org.damose.controller.GestorePannelloPreferiti;
import org.damose.model.Route;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class PannelloLinea extends JPanel {

    private final Route route;                 // Linea selezionata
    private final JCheckBox stellaCheckBox;    // Bottone per aggiungere ai preferiti
    private final JButton chiudiButton;        // Bottone per chiudere il pannello
    private final JTable tabellaFermate;       // Tabella delle fermate della linea
    private final DefaultTableModel tableModel;// Modello della tabella
    public final MappaAutobus mappa;           // Riferimento alla mappa
    public final MyFrame frame;                // Riferimento al frame principale
    private String tripCompletoId;             // ID del trip completo selezionato

    public PannelloLinea(Route route, MappaAutobus mappa, MyFrame frame) {
        this.route = route;
        this.mappa = mappa;
        this.frame = frame;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Pannello superiore con titolo e bottoni
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel titolo = new JLabel("Linea " + route.getRouteShortName());
        titolo.setFont(new Font("Arial", Font.BOLD, 18));
        titolo.setForeground(new Color(0, 0, 139));
        topPanel.add(titolo, BorderLayout.WEST);

        // Pannello destro con stella e chiudi
        JPanel destraPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        destraPanel.setBackground(Color.WHITE);

        stellaCheckBox = new JCheckBox();
        stellaCheckBox.setBackground(Color.WHITE);

        ImageIcon stellaVuota = new ImageIcon("src/main/resources/img/stella_vuota.png");
        ImageIcon stellaPiena = new ImageIcon("src/main/resources/img/stella_piena.png");
        stellaVuota = new ImageIcon(stellaVuota.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        stellaPiena = new ImageIcon(stellaPiena.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        stellaCheckBox.setIcon(stellaVuota);
        stellaCheckBox.setSelectedIcon(stellaPiena);
        stellaCheckBox.setSelected(GestorePannelloPreferiti.isPreferito(route));
        destraPanel.add(stellaCheckBox);

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

        // Legenda andata/ritorno
        JPanel legendaPanel = new JPanel();
        legendaPanel.setBackground(Color.WHITE);
        legendaPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // Quadrato blu per andata
        JPanel quadratoAndata = new JPanel();
        quadratoAndata.setBackground(new Color(0, 0, 139));
        quadratoAndata.setPreferredSize(new Dimension(18, 18));
        legendaPanel.add(quadratoAndata);

        JLabel labelAndata = new JLabel("Andata");
        labelAndata.setFont(new Font("Arial", Font.BOLD, 14));
        legendaPanel.add(labelAndata);

        // Quadrato rosso per ritorno
        JPanel quadratoRitorno = new JPanel();
        quadratoRitorno.setBackground(new Color(139, 0, 0));
        quadratoRitorno.setPreferredSize(new Dimension(18, 18));
        legendaPanel.add(quadratoRitorno);

        JLabel labelRitorno = new JLabel("Ritorno");
        labelRitorno.setFont(new Font("Arial", Font.BOLD, 14));
        legendaPanel.add(labelRitorno);

        add(legendaPanel, BorderLayout.AFTER_LAST_LINE);

        // Info base linea
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setBackground(Color.WHITE);
        info.setFont(new Font("Arial", Font.PLAIN, 15));

        String tipoLinea;
        int tipo = route.getRouteType();
        if (tipo == 0) tipoLinea = "Tram / Metro leggera";
        else if (tipo == 1) tipoLinea = "Metropolitana";
        else if (tipo == 2) tipoLinea = "Treno ferroviario";
        else if (tipo == 3) tipoLinea = "Autobus";
        else if (tipo == 4) tipoLinea = "Traghetto";
        else if (tipo == 5) tipoLinea = "Funivia";
        else if (tipo == 6) tipoLinea = "Seggiovia";
        else if (tipo == 7) tipoLinea = "Funicolare";
        else tipoLinea = "Altro (" + tipo + ")";

        info.setText("ID: " + route.getRouteId() + "\n" +
                "Nome lungo: " + route.getRouteLongName() + "\n" +
                "Tipo: " + tipoLinea);

        // Tabella fermate linea
        String[] colonne = {"N°", "Nome fermata"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabellaFermate = new JTable(tableModel);
        tabellaFermate.setFillsViewportHeight(true);
        tabellaFermate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaFermate.setFont(new Font("Arial", Font.PLAIN, 15));
        tabellaFermate.setRowHeight(22);
        tabellaFermate.setForeground(new Color(139, 0, 0));

        JTableHeader header = tabellaFermate.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 15));
        header.setForeground(new Color(139, 0, 0));
        header.setBackground(new Color(245, 245, 245));

        // Pannello centrale con info e tabella
        JPanel centroPanel = new JPanel(new BorderLayout());
        centroPanel.setBackground(Color.WHITE);
        centroPanel.add(info, BorderLayout.NORTH);
        centroPanel.add(new JScrollPane(tabellaFermate), BorderLayout.CENTER);

        add(centroPanel, BorderLayout.CENTER);
    }

    // Getter e setter
    public Route getRoute() { return route; }
    public JCheckBox getStellaCheckBox() { return stellaCheckBox; }
    public JButton getChiudiButton() { return chiudiButton; }
    public JTable getTabellaFermate() { return tabellaFermate; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public void setTripCompletoId(String id) { this.tripCompletoId = id; }
    public String getTripCompletoId() { return tripCompletoId; }
}
