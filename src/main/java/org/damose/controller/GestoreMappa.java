package org.damose.controller;

import org.damose.model.BusWaypoint;
import org.damose.model.Stop;
import org.damose.view.MappaAutobus;
import org.damose.view.MyFrame;
import org.damose.view.PannelloFermata;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * GestoreMappa si occupa di gestire la mappa dell'applicazione:
 * waypoint, percorsi, bus in tempo reale e interazioni con le fermate.
 *
 * <p>
 * Funzionalità principali:
 * <ul>
 *     <li>Centrare la mappa su una fermata</li>
 *     <li>Mostrare/nascondere waypoint e percorsi</li>
 *     <li>Aggiornare la posizione dei bus in tempo reale</li>
 *     <li>Gestire click sui waypoint per mostrare il pannello fermata</li>
 * </ul>
 * </p>
 *
 *
 */
public class GestoreMappa {

    private final MyFrame frame;                    // Riferimento al frame principale
    private static JXMapViewer mapViewer;          // Mappa sulla quale disegnare
    private static List<BusWaypoint> allWaypoints; // Tutti i waypoint visibili
    static BufferedImage pinIcon;                   // Icona dei pin sulla mappa
    private final MappaAutobus mappa;              // Riferimento alla vista della mappa
    private static Painter<JXMapViewer> routePainterCorrente = null; // Painter attuale per i percorsi o bus

    /**
     * Costruttore: inizializza la mappa e carica l'icona dei pin.
     *
     * @param view la vista {@link MappaAutobus} della mappa
     * @param mapViewer componente JXMapViewer su cui disegnare
     * @param waypoints lista di waypoint da visualizzare
     * @param frame il frame principale {@link MyFrame}
     */
    public GestoreMappa(MappaAutobus view, JXMapViewer mapViewer, List<BusWaypoint> waypoints, MyFrame frame) {
        GestoreMappa.mapViewer = mapViewer;
        GestoreMappa.allWaypoints = waypoints;
        this.frame = frame;
        this.mappa = view;

        try {
            pinIcon = ImageIO.read(new File("src/main/resources/img/pin.png")); // Carica immagine pin
        } catch (IOException e) {
            e.printStackTrace();
            pinIcon = null;
        }
    }

    /**
     * Centra la mappa sulla fermata selezionata.
     *
     * @param stop fermata su cui centrare la mappa
     */
    public static void zoomFermata(Stop stop) {
        if (stop == null || mapViewer == null) return;
        GeoPosition pos = new GeoPosition(stop.getStopLat(), stop.getStopLon());
        mapViewer.setCenterPosition(pos);
        mapViewer.setAddressLocation(pos);
        mapViewer.setZoom(0); // Zoom iniziale massimo
    }

    /**
     * Rimuove il percorso corrente ma mantiene i waypoint visibili.
     */
    public static void clearSoloPercorso() {
        if (mapViewer != null) {
            // Crea un painter solo per i waypoint
            WaypointPainter<BusWaypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(new HashSet<>(allWaypoints));
            waypointPainter.setRenderer(new ImageWaypointRenderer<>(pinIcon, 52, 52));

            mapViewer.setOverlayPainter(waypointPainter);
            routePainterCorrente = null;
            mapViewer.repaint();
        }
    }

    /**
     * Rimuove tutti i painter dalla mappa (waypoint + percorso).
     */
    public static void clearPainterLinea() {
        if (mapViewer != null) {
            mapViewer.setOverlayPainter(null);
            routePainterCorrente = null;
            mapViewer.repaint();
        }
    }

    /**
     * Mostra tutti i waypoint sulla mappa.
     */
    public static void mostraTuttiWaypoints() {
        if (mapViewer != null && allWaypoints != null) {
            WaypointPainter<BusWaypoint> painter = new WaypointPainter<>();
            painter.setWaypoints(new HashSet<>(allWaypoints));
            painter.setRenderer(new ImageWaypointRenderer<>(pinIcon, 52, 52));
            mapViewer.setOverlayPainter(painter);
            mapViewer.repaint();
        }
    }

    /**
     * Ripristina la vista iniziale della mappa: rimuove percorsi e mostra tutti i waypoint.
     */
    public static void resetVistaMappa() {
        clearPainterLinea();
        mostraTuttiWaypoints();
    }

    /**
     * Registra il listener per i click sui waypoint.
     * Quando un waypoint viene cliccato, mostra il pannello fermata corrispondente.
     */
    public void registraListener() {
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point click = e.getPoint();
                Rectangle viewport = mapViewer.getViewportBounds();

                for (BusWaypoint wp : allWaypoints) {
                    Point2D pt = mapViewer.getTileFactory().geoToPixel(wp.getPosition(), mapViewer.getZoom());
                    int x = (int) (pt.getX() - viewport.getX());
                    int y = (int) (pt.getY() - viewport.getY());

                    Rectangle area = new Rectangle(x - 10, y - 26, 20, 32);
                    if (area.contains(click)) {
                        clearSoloPercorso();

                        Stop stop = wp.getStop();
                        PannelloFermata pannello = new PannelloFermata(stop);
                        new GestorePannelloFermata(pannello, frame, mappa);
                        frame.aggiornaPannello(pannello, wp);
                        return;
                    }
                }
            }
        });
    }

    /**
     * Renderer personalizzato per disegnare immagini come waypoint sulla mappa.
     *
     * @param <T> tipo di waypoint esteso da {@link Waypoint}
     */
    static class ImageWaypointRenderer<T extends Waypoint> implements WaypointRenderer<T> {
        private final BufferedImage image;
        private final int width;
        private final int height;

        public ImageWaypointRenderer(BufferedImage image, int width, int height) {
            this.image = image;
            this.width = width;
            this.height = height;
        }

        @Override
        public void paintWaypoint(Graphics2D g, JXMapViewer map, T wp) {
            if (image == null) return;
            Point2D point = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
            int x = (int) point.getX() - width / 2;
            int y = (int) point.getY() - height;
            g.drawImage(image, x, y, width, height, null);
        }
    }

    /**
     * Mostra i bus in tempo reale sopra i waypoint e il percorso.
     *
     * @param busWaypoints lista di bus da visualizzare
     */
    public static void mostraBusSuMappa(List<BusWaypoint> busWaypoints) {
        if (mapViewer == null) return;

        try {
            BufferedImage busIcon = ImageIO.read(new File("src/main/resources/img/bus.png"));

            WaypointPainter<BusWaypoint> busPainter = new WaypointPainter<>();
            busPainter.setWaypoints(new HashSet<>(busWaypoints));
            busPainter.setRenderer(new ImageWaypointRenderer<>(busIcon, 42, 42));

            // Mantieni eventuali painter correnti
            java.util.List<Painter<JXMapViewer>> painters = new java.util.ArrayList<>();
            Painter<JXMapViewer> currentPainter = (Painter<JXMapViewer>) mapViewer.getOverlayPainter();

            if (currentPainter instanceof org.jxmapviewer.painter.CompoundPainter) {
                org.jxmapviewer.painter.CompoundPainter<JXMapViewer> oldCompound =
                        (org.jxmapviewer.painter.CompoundPainter<JXMapViewer>) currentPainter;

                for (Painter<JXMapViewer> p : oldCompound.getPainters()) {
                    if (p != routePainterCorrente) painters.add(p);
                }
            } else if (currentPainter != null && currentPainter != routePainterCorrente) {
                painters.add(currentPainter);
            }

            painters.add(busPainter);
            routePainterCorrente = busPainter;

            mapViewer.setOverlayPainter(new org.jxmapviewer.painter.CompoundPainter<>(painters));
            mapViewer.repaint();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
