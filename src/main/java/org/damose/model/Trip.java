package org.damose.model;

/**
 * Rappresenta un singolo viaggio (trip) di una linea di trasporto pubblico.
 * Contiene informazioni sulla linea, direzione, shape, accessibilità e altre caratteristiche.
 */
public class Trip {

    /** ID della linea a cui appartiene il trip */
    private String routeId;

    /** ID del servizio (es. calendario o eccezioni) */
    private String serviceId;

    /** ID univoco del trip */
    private String tripId;

    /** Indicazione della destinazione o headsign del trip */
    private String tripHeadsign;

    /** Nome breve del trip (facoltativo) */
    private String tripShortName;

    /** Direzione del trip (0 o 1) */
    private int directionId;

    /** ID del blocco a cui appartiene il trip (facoltativo) */
    private String blockId;

    /** ID della shape associata al percorso del trip */
    private String shapeId;

    /** Indica se il trip è accessibile alle sedie a rotelle (0=non accessibile, 1=accessibile) */
    private int wheelchairAccessible;

    /** Indicatore di trip eccezionale o speciale (0=normale, 1=eccezionale) */
    private int exceptional;

    /**
     * Costruttore.
     *
     * @param routeId ID della linea
     * @param serviceId ID del servizio
     * @param tripId ID del trip
     * @param tripHeadsign HeadSign o destinazione
     * @param tripShortName Nome breve del trip
     * @param directionId Direzione (0 o 1)
     * @param blockId ID del blocco
     * @param shapeId ID della shape
     * @param wheelchairAccessible Accessibilità disabili
     * @param exceptional Trip eccezionale
     */
    public Trip(String routeId, String serviceId, String tripId, String tripHeadsign,
                String tripShortName, int directionId, String blockId, String shapeId,
                int wheelchairAccessible, int exceptional) {

        this.routeId = routeId;
        this.serviceId = serviceId;
        this.tripId = tripId;
        this.tripHeadsign = tripHeadsign;
        this.tripShortName = tripShortName;
        this.directionId = directionId;
        this.blockId = blockId;
        this.shapeId = shapeId;
        this.wheelchairAccessible = wheelchairAccessible;
        this.exceptional = exceptional;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getTripId() {
        return tripId;
    }

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public String getTripShortName() {
        return tripShortName;
    }

    public int getDirectionId() {
        return directionId;
    }

    public String getBlockId() {
        return blockId;
    }

    public String getShapeId() {
        return shapeId;
    }

    public int getWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public int getExceptional() {
        return exceptional;
    }
}
