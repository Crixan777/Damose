package org.damose.model;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

/**
 * Rappresenta un waypoint sulla mappa per un autobus.
 * Può essere associato a una fermata specifica o essere solo una posizione generica.
 */
public class BusWaypoint implements Waypoint {

    /** Posizione geografica del waypoint */
    private final GeoPosition position;

    /** Fermata associata al waypoint (può essere null se non rilevante) */
    private final Stop stop;

    /**
     * Costruttore.
     *
     * @param lat Latitudine del waypoint
     * @param lon Longitudine del waypoint
     * @param stop Fermata associata (null se non c'è)
     */
    public BusWaypoint(double lat, double lon, Stop stop) {
        this.position = new GeoPosition(lat, lon);
        this.stop = stop;
    }

    /**
     * Restituisce la posizione geografica del waypoint.
     *
     * @return Posizione (latitudine e longitudine)
     */
    @Override
    public GeoPosition getPosition() {
        return position;
    }

    /**
     * Restituisce la fermata associata a questo waypoint.
     *
     * @return Fermata o null se non presente
     */
    public Stop getStop() {
        return stop;
    }
}
