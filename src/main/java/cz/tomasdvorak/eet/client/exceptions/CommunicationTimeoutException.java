package cz.tomasdvorak.eet.client.exceptions;

import cz.etrzby.xml.TrzbaType;

public class CommunicationTimeoutException extends CommunicationException {
    public CommunicationTimeoutException(final TrzbaType request, final Throwable t) {
        super(request, t);
    }
}
