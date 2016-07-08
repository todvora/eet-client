package cz.tomasdvorak.eet.client.exceptions;

public class InvalidKeystoreException extends Exception {
    public InvalidKeystoreException(final String message) {
        super(message);
    }

    public InvalidKeystoreException(final Exception cause) {
        super(cause);
    }
}
