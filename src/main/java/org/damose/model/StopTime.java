package org.damose.model;

/**
 * Rappresenta un singolo passaggio di un trip su una fermata.
 * Contiene orari di arrivo/partenza, sequenza della fermata e informazioni aggiuntive.
 */
public class StopTime {

    /** ID del trip a cui appartiene questo stop time */
    private String tripId;

    /** Orario di arrivo (HH:mm:ss) */
    private String arrivalTime;

    /** Orario di partenza (HH:mm:ss) */
    private String departureTime;

    /** ID della fermata */
    private String stopId;

    /** Sequenza della fermata all'interno del trip */
    private int stopSequence;

    /** Indicazione del headsign della fermata (ad esempio direzione visualizzata) */
    private String stopHeadsign;

    /** Tipo di pickup (0=nessuno, 1=regolare, ecc.) */
    private int pickupType;

    /** Tipo di drop-off (0=nessuno, 1=regolare, ecc.) */
    private int dropOffType;

    /** Distanza percorsa lungo la shape fino a questa fermata */
    private double shapeDistTraveled;

    /** Indica se l'orario è esatto o approssimativo */
    private int timepoint;

    /**
     * Costruttore.
     *
     * @param tripId ID del trip
     * @param arrivalTime Orario di arrivo
     * @param departureTime Orario di partenza
     * @param stopId ID della fermata
     * @param stopSequence Sequenza della fermata
     * @param stopHeadsign HeadSign della fermata
     * @param pickupType Tipo di pickup
     * @param dropOffType Tipo di drop-off
     * @param shapeDistTraveled Distanza percorsa sulla shape
     * @param timepoint Indicatore di precisione dell'orario
     */
    public StopTime(String tripId, String arrivalTime, String departureTime, String stopId,
                    int stopSequence, String stopHeadsign, int pickupType, int dropOffType,
                    double shapeDistTraveled, int timepoint) {

        this.tripId = tripId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopId = stopId;
        this.stopSequence = stopSequence;
        this.stopHeadsign = stopHeadsign;
        this.pickupType = pickupType;
        this.dropOffType = dropOffType;
        this.shapeDistTraveled = shapeDistTraveled;
        this.timepoint = timepoint;
    }

    public String getTripId() {
        return tripId;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getStopId() {
        return stopId;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public String getStopHeadsign() {
        return stopHeadsign;
    }

    public int getPickupType() {
        return pickupType;
    }

    public int getDropOffType() {
        return dropOffType;
    }

    public double getShapeDistTraveled() {
        return shapeDistTraveled;
    }

    public int getTimepoint() {
        return timepoint;
    }
}
