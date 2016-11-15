package cz.tomasdvorak.eet.client.exceptions;

/**
 * Provided data entry is not in valid format. For example unparsable date, invalid URL and so one.
 */
public class InvalidFormatException extends RuntimeException {
    public InvalidFormatException(final Throwable cause) {
        super(cause);
    }
}
