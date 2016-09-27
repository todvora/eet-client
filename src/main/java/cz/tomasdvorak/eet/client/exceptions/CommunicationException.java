package cz.tomasdvorak.eet.client.exceptions;

import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.utils.StringUtils;

/**
 * Every possible fault caused during communication with EET service. Contains original request, suitable for
 * processing PKP code in case of failure (is required to be printed on the receipt in case of connection failure).
 */
public class CommunicationException extends Exception {

    private final TrzbaType request;

    public CommunicationException(final TrzbaType request, final Throwable t) {
        super(t);
        this.request = request;
    }

    public TrzbaType getRequest() {
        return request;
    }

    public String getPKP() {
        return StringUtils.toBase64(request.getKontrolniKody().getPkp().getValue());
    }
}
