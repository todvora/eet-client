package cz.tomasdvorak.eet.client.config;

public enum EndpointType {
    PRODUCTION("https://prod.eet.cz:443/eet/services/EETServiceSOAP/v3"),
    PLAYGROUND("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3");

    private final String webserviceUrl;

    EndpointType(final String webserviceUrl) {
        this.webserviceUrl = webserviceUrl;
    }

    public String getWebserviceUrl() {
        return webserviceUrl;
    }
}
