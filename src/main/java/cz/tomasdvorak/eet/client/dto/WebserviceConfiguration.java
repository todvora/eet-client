package cz.tomasdvorak.eet.client.dto;

public class WebserviceConfiguration {

    public static final WebserviceConfiguration DEFAULT = new WebserviceConfiguration(
            2000L // timeout for webservice call in millis = 2s, required by the current laws
    );
    private final long receiveTimeout;

    /**
     * @param receiveTimeout receiving timeout of the Webservice call in millis
     */
    public WebserviceConfiguration(final long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }
}
