package cz.tomasdvorak.eet.client.exceptions;

public class RevocationListException extends Exception {

    public RevocationListException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RevocationListException(final Throwable cause) {
        super(cause);
    }
}
