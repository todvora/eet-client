package cz.tomasdvorak.eet.client.exceptions;

import javax.xml.bind.JAXBException;

/**
 * Failed to de/serialize EET request from or to String.
 */
public class RequestSerializationException extends RuntimeException {
    public RequestSerializationException(final String message, final JAXBException cause) {
        super(message, cause);

    }
}
