package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.TrzbaDataType;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.config.SubmissionType;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.security.ServerKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

@Category(IntegrationTest.class)
public class BackwardsCompatibilityTest {

    private EETClient eetService;



    @Before
    public void setUp() throws Exception {
        final InputStream clientKey = getClass().getResourceAsStream("/keys/CZ683555118.p12");
        final InputStream intermediateCa = getClass().getResourceAsStream("/certificates/2qca16_rsa.der");
        final InputStream rootCa = getClass().getResourceAsStream("/certificates/rca15_rsa.der");
        this.eetService = EETServiceFactory.getInstance(clientKey, "eet", rootCa, intermediateCa);
    }


    @Test
    public void realCommunicationPlayground() throws Exception {
        final TrzbaDataType data = getData();
        final SubmitResult result = eetService.submitReceipt(data, CommunicationMode.REAL, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
        Assert.assertNull(result.getChyba());
        Assert.assertNotNull(result.getFik());
        final String bkpFromRequest = result.getBKP();
        final String bkpFromResponse = result.getHlavicka().getBkp();
        Assert.assertEquals(bkpFromRequest, bkpFromResponse);
    }

    @Test
    public void testCommunicationPlayground() throws Exception {
        final TrzbaDataType data = getData();
        final SubmitResult result = eetService.submitReceipt(data, CommunicationMode.TEST, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
        Assert.assertNull(result.getPotvrzeni());
        Assert.assertEquals("Datovou zpravu evidovane trzby v overovacim modu se podarilo zpracovat", result.getChyba().getContent());
        Assert.assertEquals(0, result.getChyba().getKod());
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
