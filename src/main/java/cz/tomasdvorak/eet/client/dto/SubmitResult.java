package cz.tomasdvorak.eet.client.dto;

import cz.etrzby.xml.OdpovedType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.utils.StringUtils;

import javax.xml.bind.DatatypeConverter;

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

    public String getBKP() {
        return getHlavicka().getBkp();
    }

    public String getPKP() {
        return StringUtils.toBase64(request.getKontrolniKody().getPkp().getValue());
    }

    public String getFik() {
        return getPotvrzeni().getFik();
    }
}
