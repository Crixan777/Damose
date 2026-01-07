package org.damose.view;

import org.damose.controller.GestoreMappa;
import org.damose.controller.MyParser;
import org.damose.model.*;
import org.damose.model.Shape;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

/**
 * Pannello contenente la mappa interattiva con fermate, linee e viaggi.
 * Utilizza JXMapViewer per visualizzare la mappa e le fermate con waypoint.
 */
public class MappaAutobus extends JPanel {

    /** Componente principale della mappa */
    private final JXMapViewer mapViewer;

    /** Lista di waypoint per tutte le fermate */
    private final List<BusWaypoint> waypoints;

    /** Riferimento al frame principale */
    private final MyFrame frame;

    /** Liste dei dati GTFS caricati */
    private final List<Stop> stops;
    private final List<Route> routes;
    private final List<Trip> trips;
    private final List<Shape> shapes;
    private final List<StopTime> stopTimes;

    /**
     * Costruttore.
     * Inizializza la mappa, carica i dati GTFS e crea i waypoint.
     *
     * @param frame il frame principale a cui appartiene la mappa
     */
    public MappaAutobus(MyFrame frame) {
        this.setLayout(new BorderLayout());
        this.frame = frame;

        // Configurazione tile factory (OpenStreetMap)
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setLocalCache(new FileBasedLocalCache(
                new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2"), false));

        // Inizializzazione JXMapViewer
        mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        // Imposta posizione iniziale (Roma) e zoom
        GeoPosition iniziale = new GeoPosition(41.9028, 12.4964);
        mapViewer.setZoom(3);
        mapViewer.setAddressLocation(iniziale);

        // Abilita pan e zoom
        PanMouseInputListener panListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        // Inizializza liste dati
        waypoints = new ArrayList<>();
        stops = new ArrayList<>();
        routes = new ArrayList<>();
        trips = new ArrayList<>();
        shapes = new ArrayList<>();
        stopTimes = new ArrayList<>();

        // Caricamento dati GTFS statici (senza stop_times)
        try {
            List<Stop> parsedStops = MyParser.parseStops("src/main/resources/gtfsStatici/stops.txt");
            if (parsedStops != null) {
                stops.addAll(parsedStops);
                for (Stop stop : parsedStops) {
                    waypoints.add(new BusWaypoint(stop.getStopLat(), stop.getStopLon(), stop));
                }
            }

            routes.addAll(MyParser.parseRoutes("src/main/resources/gtfsStatici/routes.txt"));
            trips.addAll(MyParser.parseTrips("src/main/resources/gtfsStatici/trips.txt"));
            shapes.addAll(MyParser.parseShapes("src/main/resources/gtfsStatici/shapes.txt"));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento dei dati GTFS: " + e.getMessage());
        }

        // Caricamento stop_times limitato alla fascia oraria desiderata (in background)
        new SwingWorker<List<StopTime>, Void>() {
            @Override
            protected List<StopTime> doInBackground() {
                try {
                    LocalTime now = LocalTime.now();
                    return MyParser.parseStopTimesByHour(
                            "src/main/resources/gtfsStatici/stop_times.txt",
                            now.minusHours(1), now.plusHours(2)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    List<StopTime> result = get();
                    stopTimes.addAll(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();

        // Carica icona per i waypoint
        BufferedImage pinIcon;
        try {
            pinIcon = ImageIO.read(new File("src/main/resources/img/pin.png"));
        } catch (IOException e) {
            pinIcon = null;
        }

        // Crea il painter dei waypoint
        WaypointPainter<BusWaypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(new HashSet<>(waypoints));
        waypointPainter.setRenderer(new ImageWaypointRenderer<>(pinIcon, 52, 52));

        mapViewer.setOverlayPainter(waypointPainter);

        // Aggiunge la mappa al pannello
        add(mapViewer, BorderLayout.CENTER);

        // Inizializza il gestore della mappa (listener per interazioni)
        GestoreMappa gestoreMappa = new GestoreMappa(this, mapViewer, waypoints, frame);
        gestoreMappa.registraListener();
    }

    // Getter per i dati GTFS
    public List<Stop> getFermate() { return stops; }
    public List<Route> getLinee() { return routes; }
    public List<Trip> getTrips() { return trips; }
    public List<Shape> getShapes() { return shapes; }
    public List<StopTime> getStopTimes() { return stopTimes; }
    public List<BusWaypoint> getWaypoints() { return waypoints; }
    public JXMapViewer getMapViewer() { return mapViewer; }

    /**
     * Renderer personalizzato per disegnare waypoint come immagini PNG.
     *
     * @param <T> tipo di waypoint
     */
    private static class ImageWaypointRenderer<T extends Waypoint> implements WaypointRenderer<T> {
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
}
