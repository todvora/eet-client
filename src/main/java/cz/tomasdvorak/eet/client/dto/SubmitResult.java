package cz.tomasdvorak.eet.client.dto;

import cz.etrzby.xml.OdpovedType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.persistence.RequestSerializer;
import cz.tomasdvorak.eet.client.utils.StringUtils;

/**
 * Communication result holding all response data and additionally also all request data.
 * There are defined some additional helper methods for easier access to usual fields (FIK, PKP, BKP)
 */
public class SubmitResult extends OdpovedType {

    private final TrzbaType request;

    public SubmitResult(final TrzbaType request, final OdpovedType response) {
        super();
        setChyba(response.getChyba());
        setHlavicka(response.getHlavicka());
        setPotvrzeni(response.getPotvrzeni());
        withVarovani(response.getVarovani());
        this.request = request;
    }

    /**
     * Get the original request to this response
     */
    public TrzbaType getRequest() {
        return request;
    }

    /**
     * Convert the current request to a String representation. Useful for persisting and/or logging purposes.
     * @return String representation of current request.
     */
    public String serializeRequest()  {
        return RequestSerializer.toString(request);
    }

    /**
     * Utility method for easier access to BKP (Taxpayer's Security Code) security code
     * @return BKP code from the request
     */
    public String getBKP() {
        return request.getKontrolniKody().getBkp().getValue();
    }

    /**
     * Utility method for easier access to PKP (Taxpayer's Signature Code) security code
     * @return PKP code, encoded as Base64, suitable for printing on the receipt in case of communication failure.
     */
    public String getPKP() {
        return StringUtils.toBase64(request.getKontrolniKody().getPkp().getValue());
    }

    /**
     * Utility method for easier access to FIK (Fiscal Identification Code)
     * @return FIK code from the response. May be {@code null}, if the response contains errors
     */
    public String getFik() {
        return getPotvrzeni() == null ? null : getPotvrzeni().getFik();
    }
}
