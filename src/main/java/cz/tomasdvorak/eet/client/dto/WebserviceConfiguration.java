package cz.tomasdvorak.eet.client.dto;

/**
 * TODO: create builder for the configuration!
 */
public class WebserviceConfiguration {


    public static final long RECEIVE_TIMEOUT = 2000L;
    public static final long DNS_LOOKUP_TIMEOUT = 2000L;

    public static final WebserviceConfiguration DEFAULT = new WebserviceConfiguration(
            RECEIVE_TIMEOUT, // timeout for webservice call in millis = 2s, required by the current laws
            DNS_LOOKUP_TIMEOUT  // timeout for DNS lookup in millis = 2s
    );
    private long receiveTimeout;
    private long dnsLookupTimeout;

    /**
     * @param receiveTimeout receiving timeout of the Webservice call in millis
     */
    public WebserviceConfiguration(final long receiveTimeout) {
        this(receiveTimeout, DNS_LOOKUP_TIMEOUT);
    }

    /**
     * @param receiveTimeout receiving timeout of the Webservice call in millis
     * @param dnsLookupTimeout timeout for DNS lookup in millis
     */
    public WebserviceConfiguration(final long receiveTimeout, final long dnsLookupTimeout) {
        this.receiveTimeout = receiveTimeout;
        this.dnsLookupTimeout = dnsLookupTimeout;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public long getDnsLookupTimeout() {
        return dnsLookupTimeout;
    }
}
