package cz.tomasdvorak.eet.client.exceptions;

/**
 * DNS lookup failed after timeout reached.
 */
public class DnsTimeoutException extends Exception {
    public DnsTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
