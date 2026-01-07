package org.damose.model;

/**
 * Rappresenta un punto di percorso (shape) per una linea di trasporto pubblico.
 * Contiene coordinate geografiche, sequenza del punto e distanza percorsa.
 */
public class Shape {

    /** ID dello shape a cui questo punto appartiene */
    private String shapeId;

    /** Latitudine del punto */
    private double shapePtLat;

    /** Longitudine del punto */
    private double shapePtLon;

    /** Sequenza del punto nello shape (ordine del percorso) */
    private int shapePtSequence;

    /** Distanza percorsa fino a questo punto (in unità definite dal feed GTFS) */
    private double shapeDistTraveled;

    /**
     * Costruttore.
     *
     * @param shapeId ID dello shape
     * @param shapePtLat Latitudine
     * @param shapePtLon Longitudine
     * @param shapePtSequence Sequenza del punto
     * @param shapeDistTraveled Distanza percorsa fino a questo punto
     */
    public Shape(String shapeId, double shapePtLat, double shapePtLon,
                 int shapePtSequence, double shapeDistTraveled) {
        this.shapeId = shapeId;
        this.shapePtLat = shapePtLat;
        this.shapePtLon = shapePtLon;
        this.shapePtSequence = shapePtSequence;
        this.shapeDistTraveled = shapeDistTraveled;
    }

    public String getShapeId() {
        return shapeId;
    }

    public double getShapePtLat() {
        return shapePtLat;
    }

    public double getShapePtLon() {
        return shapePtLon;
    }

    public int getShapePtSequence() {
        return shapePtSequence;
    }

    public double getShapeDistTraveled() {
        return shapeDistTraveled;
    }
}
