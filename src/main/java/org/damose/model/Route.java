package org.damose.model;

/**
 * Rappresenta una linea di trasporto pubblico.
 * Contiene informazioni identificative, colore, URL e tipo di linea.
 */
public class Route {

    /** ID univoco della linea */
    private String routeId;

    /** ID dell'agenzia che gestisce la linea */
    private String agencyId;

    /** Nome breve della linea (es. "H") */
    private String routeShortName;

    /** Nome completo della linea (es. "Linea H Termini - Anagnina") */
    private String routeLongName;

    /** Tipo di linea (es. autobus, tram, metropolitana) */
    private int routeType;

    /** URL della linea */
    private String routeUrl;

    /** Colore principale della linea (hex) */
    private String routeColor;

    /** Colore del testo associato alla linea (hex) */
    private String routeTextColor;

    /**
     * Costruttore.
     *
     * @param routeId ID della linea
     * @param agencyId ID dell'agenzia
     * @param routeShortName Nome breve della linea
     * @param routeLongName Nome completo della linea
     * @param routeType Tipo della linea
     * @param routeUrl URL della linea
     * @param routeColor Colore della linea
     * @param routeTextColor Colore del testo della linea
     */
    public Route(String routeId, String agencyId, String routeShortName, String routeLongName,
                 int routeType, String routeUrl, String routeColor, String routeTextColor) {
        this.routeId = routeId;
        this.agencyId = agencyId;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.routeType = routeType;
        this.routeUrl = routeUrl;
        this.routeColor = routeColor;
        this.routeTextColor = routeTextColor;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public int getRouteType() {
        return routeType;
    }

    public String getRouteUrl() {
        return routeUrl;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public String getRouteTextColor() {
        return routeTextColor;
    }
}
