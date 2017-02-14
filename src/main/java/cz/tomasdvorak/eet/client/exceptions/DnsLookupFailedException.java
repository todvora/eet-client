package cz.tomasdvorak.eet.client.exceptions;

/**
 * DNS lookup failed from some other reason than timeout. It may be UnknownHostException, MalformedURLException or similar.
 */
public class DnsLookupFailedException extends Exception {
    public DnsLookupFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
