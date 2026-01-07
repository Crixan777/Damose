package org.damose.model;

/**
 * Rappresenta una data speciale del calendario dei servizi di trasporto.
 * Può indicare giorni in cui il servizio è aggiunto o rimosso (eccezioni).
 */
public class CalendarDate {

    /** ID del servizio associato */
    private String serviceId;

    /** Data in formato YYYYMMDD */
    private String date;

    /** Tipo di eccezione:
     * 1 = servizio aggiunto,
     * 2 = servizio rimosso
     */
    private int exceptionType;

    /**
     * Costruttore.
     *
     * @param serviceId ID del servizio
     * @param date Data della eccezione (YYYYMMDD)
     * @param exceptionType Tipo di eccezione (1=aggiunto, 2=rimosso)
     */
    public CalendarDate(String serviceId, String date, int exceptionType) {
        this.serviceId = serviceId;
        this.date = date;
        this.exceptionType = exceptionType;
    }

    /**
     * Restituisce l'ID del servizio.
     * @return serviceId
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Restituisce la data della eccezione.
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     * Restituisce il tipo di eccezione.
     * @return exceptionType
     */
    public int getExceptionType() {
        return exceptionType;
    }
}
