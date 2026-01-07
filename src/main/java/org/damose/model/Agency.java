package org.damose.model;

/**
 * Rappresenta un'agenzia di trasporto pubblico.
 * Contiene informazioni come nome, URL, timezone, lingua e contatti.
 */
public class Agency {

    /** Identificativo univoco dell'agenzia */
    private String agencyId;

    /** Nome dell'agenzia */
    private String agencyName;

    /** URL ufficiale dell'agenzia */
    private String agencyUrl;

    /** Fuso orario dell'agenzia */
    private String agencyTimezone;

    /** Lingua principale dell'agenzia */
    private String agencyLang;

    /** Numero di telefono di contatto */
    private String agencyPhone;

    /** URL per informazioni su tariffe e biglietti */
    private String agencyFareUrl;

    /**
     * Costruttore completo.
     *
     * @param agencyId Identificativo univoco
     * @param agencyName Nome dell'agenzia
     * @param agencyUrl URL ufficiale
     * @param agencyTimezone Fuso orario
     * @param agencyLang Lingua principale
     * @param agencyPhone Contatto telefonico
     * @param agencyFareUrl URL informazioni tariffe
     */
    public Agency(String agencyId, String agencyName, String agencyUrl, String agencyTimezone,
                  String agencyLang, String agencyPhone, String agencyFareUrl) {

        this.agencyId = agencyId;
        this.agencyName = agencyName;
        this.agencyUrl = agencyUrl;
        this.agencyTimezone = agencyTimezone;
        this.agencyLang = agencyLang;
        this.agencyPhone = agencyPhone;
        this.agencyFareUrl = agencyFareUrl;
    }

    /** @return Identificativo univoco dell'agenzia */
    public String getAgencyId() {
        return agencyId;
    }

    /** @return Nome dell'agenzia */
    public String getAgencyName() {
        return agencyName;
    }

    /** @return URL ufficiale dell'agenzia */
    public String getAgencyUrl() {
        return agencyUrl;
    }

    /** @return Fuso orario dell'agenzia */
    public String getAgencyTimezone() {
        return agencyTimezone;
    }

    /** @return Lingua principale dell'agenzia */
    public String getAgencyLang() {
        return agencyLang;
    }

    /** @return Numero di telefono di contatto */
    public String getAgencyPhone() {
        return agencyPhone;
    }

    /** @return URL per informazioni su tariffe e biglietti */
    public String getAgencyFareUrl() {
        return agencyFareUrl;
    }
}
