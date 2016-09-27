package cz.tomasdvorak.eet.client.exceptions;

/**
 * Exception thrown during computation of PKP and BKP security codes. May be related to problems with certificate,
 * unknown security algorithm or any other signing related issue.
 *
 * The exception is not recoverable and indicates a configuration error. Either of the certificates, keys and their location
 * or the Java runtime environment itself.
 *
 * @see cz.tomasdvorak.eet.client.security.ClientKey#sign(String)
 */
public class DataSigningException extends Exception {
    public DataSigningException(final Throwable cause) {
        super(cause);
    }
}
