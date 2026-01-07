package org.damose.model;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * Rappresenta una fermata di trasporto pubblico.
 * Contiene informazioni identificative, coordinate geografiche e attributi accessori.
 */
public class Stop {

    /** ID univoco della fermata */
    private String stopId;

    /** Codice della fermata (se disponibile) */
    private String stopCode;

    /** Nome della fermata */
    private String stopName;

    /** Descrizione aggiuntiva della fermata */
    private String stopDesc;

    /** Latitudine della fermata */
    private double stopLat;

    /** Longitudine della fermata */
    private double stopLon;

    /** URL informativo della fermata */
    private String stopUrl;

    /** Indicatore accessibilità per sedie a rotelle (0=non specificato, 1=accessibile, 2=non accessibile) */
    private int wheelchairBoarding;

    /** Fuso orario della fermata */
    private String stopTimezone;

    /** Tipo di fermata (0=standard, 1=stazione principale, ecc.) */
    private int locationType;

    /** ID della stazione principale se questa fermata è parte di un gruppo */
    private String parentStation;

    /**
     * Costruttore.
     *
     * @param stopId ID univoco della fermata
     * @param stopCode Codice della fermata
     * @param stopName Nome della fermata
     * @param stopDesc Descrizione della fermata
     * @param stopLat Latitudine
     * @param stopLon Longitudine
     * @param stopUrl URL della fermata
     * @param wheelchairBoarding Indicatore accessibilità
     * @param stopTimezone Fuso orario
     * @param locationType Tipo di fermata
     * @param parentStation ID della stazione principale
     */
    public Stop(String stopId, String stopCode, String stopName, String stopDesc,
                double stopLat, double stopLon, String stopUrl, int wheelchairBoarding,
                String stopTimezone, int locationType, String parentStation) {

        this.stopId = stopId;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopDesc = stopDesc;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.stopUrl = stopUrl;
        this.wheelchairBoarding = wheelchairBoarding;
        this.stopTimezone = stopTimezone;
        this.locationType = locationType;
        this.parentStation = parentStation;
    }

    public String getStopId() {
        return stopId;
    }

    public String getStopCode() {
        return stopCode;
    }

    public String getStopName() {
        return stopName;
    }

    public String getStopDesc() {
        return stopDesc;
    }

    public double getStopLat() {
        return stopLat;
    }

    public double getStopLon() {
        return stopLon;
    }

    public String getStopUrl() {
        return stopUrl;
    }

    public int getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public String getStopTimezone() {
        return stopTimezone;
    }

    public int getLocationType() {
        return locationType;
    }

    public String getParentStation() {
        return parentStation;
    }
}
