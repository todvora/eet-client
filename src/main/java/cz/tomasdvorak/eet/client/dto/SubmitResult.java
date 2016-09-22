package cz.tomasdvorak.eet.client.dto;

import cz.etrzby.xml.OdpovedType;
import cz.etrzby.xml.TrzbaType;

import javax.xml.bind.DatatypeConverter;

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

    public TrzbaType getRequest() {
        return request;
    }

    public String getBKP() {
        return getHlavicka().getBkp();
    }

    public String getPKP() {
        return DatatypeConverter.printBase64Binary(request.getKontrolniKody().getPkp().getValue());
    }

    public String getFik() {
        return getPotvrzeni().getFik();
    }
}
