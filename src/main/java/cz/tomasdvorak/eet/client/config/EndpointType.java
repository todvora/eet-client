package cz.tomasdvorak.eet.client.config;

public enum EndpointType {
    // TODO: add real endpoint, when known
    PLAYGROUND("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v2");

    private final String webserviceUrl;

    EndpointType(final String webserviceUrl) {
        this.webserviceUrl = webserviceUrl;
    }

    public String getWebserviceUrl() {
        return webserviceUrl;
    }
}
