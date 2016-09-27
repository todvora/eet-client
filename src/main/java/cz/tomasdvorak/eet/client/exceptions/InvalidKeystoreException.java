package cz.tomasdvorak.eet.client.exceptions;

/**
 * Keystore / Certstore exception thrown, if the application cannot process provided files and their content.
 *
 * The exception is not recoverable and indicates a configuration error.
 */
public class InvalidKeystoreException extends Exception {
    public InvalidKeystoreException(final String message) {
        super(message);
    }

    public InvalidKeystoreException(final Exception cause) {
        super(cause);
    }
}
