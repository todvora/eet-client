package cz.tomasdvorak.eet.client.persistence;

import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.EETClient;
import cz.tomasdvorak.eet.client.EETServiceFactory;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.SubmissionType;
import cz.tomasdvorak.eet.client.utils.NumberUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

public class RequestSerializerTest {

    private EETClient eetService;

    @Before
    public void setUp() throws Exception {
        /*
          Client's key pair, used to sign requests
         */
        final InputStream clientKey = getClass().getResourceAsStream("/keys/CZ683555118.p12");

        /*
          EET's server certificate, issued by I.CA, used to verify response signature
         */
        final InputStream serverCertificate = getClass().getResourceAsStream("/keys/qica.der");

        this.eetService = EETServiceFactory.getInstance(clientKey, "eet", serverCertificate);
    }

    @Test
    public void testSerializeDeserialize() throws Exception {
        final TrzbaType request = eetService.prepareRequest(getData(), CommunicationMode.REAL, SubmissionType.FIRST_ATTEMPT);

        final String bkp = request.getKontrolniKody().getBkp().getValue();
        final byte[] pkp = request.getKontrolniKody().getPkp().getValue();
        final String uuid = request.getHlavicka().getUuidZpravy();

        final String serialized = RequestSerializer.toString(request);
        final TrzbaType restored = RequestSerializer.fromString(serialized);

        Assert.assertEquals("CZ683555118", restored.getData().getDicPopl());
        Assert.assertEquals(243, restored.getData().getIdProvoz());
        Assert.assertEquals("24/A-6/Brno_2", restored.getData().getIdPokl());
        Assert.assertEquals("3264.00", NumberUtils.format(restored.getData().getCelkTrzba()));

        Assert.assertEquals(bkp, restored.getKontrolniKody().getBkp().getValue());
        Assert.assertArrayEquals(pkp, restored.getKontrolniKody().getPkp().getValue());

        Assert.assertEquals(uuid, restored.getHlavicka().getUuidZpravy());

    }

    private TrzbaDataType getData() {
        return new TrzbaDataType()
                .withDicPopl("CZ683555118")
                .withIdProvoz(243)
                .withIdPokl("24/A-6/Brno_2")
                .withPoradCis("#135433c/11/2016")
                .withDatTrzby(new Date())
                .withCelkTrzba(new BigDecimal("3264"));
    }

}