package cz.tomasdvorak.eet.client.exceptions;

import cz.etrzby.xml.TrzbaType;
import org.apache.cxf.common.i18n.Exception;

public class CommunicationException extends Exception {

    private final TrzbaType request;

    public CommunicationException(final TrzbaType request, final Throwable t) {
        super(t);
        this.request = request;
    }

    public TrzbaType getRequest() {
        return request;
    }
}
