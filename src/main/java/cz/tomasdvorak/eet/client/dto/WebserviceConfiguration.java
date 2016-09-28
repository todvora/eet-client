package cz.tomasdvorak.eet.client.dto;

public class WebserviceConfiguration {

    public static final WebserviceConfiguration DEFAULT = new WebserviceConfiguration(
            10000L // timeout for webservice call in millis = 10s
    );
    private final long receiveTimeout;

    public WebserviceConfiguration(final long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }
}
