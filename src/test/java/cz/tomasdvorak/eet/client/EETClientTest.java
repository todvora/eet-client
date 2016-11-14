package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.config.SubmissionType;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.dto.WebserviceConfiguration;
import cz.tomasdvorak.eet.client.exceptions.CommunicationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

@Category(IntegrationTest.class)
public class EETClientTest {

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
    public void realCommunication() throws Exception {
        final TrzbaDataType data = getData();
        final SubmitResult result = eetService.submitReceipt(data, CommunicationMode.REAL, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
        Assert.assertNull(result.getChyba());
        Assert.assertNotNull(result.getFik());
    }

    @Test
    public void testCommunication() throws Exception {
        final TrzbaDataType data = getData();
        final SubmitResult result = eetService.submitReceipt(data, CommunicationMode.TEST, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
        Assert.assertNull(result.getPotvrzeni());
        Assert.assertEquals("Datovou zpravu evidovane trzby v overovacim modu se podarilo zpracovat", result.getChyba().getContent());
        Assert.assertEquals(0, result.getChyba().getKod());
    }

    @Test
    public void testTimeoutHandling() throws Exception {
        final InputStream clientKey = getClass().getResourceAsStream("/keys/CZ683555118.p12");
        final InputStream serverCertificate = getClass().getResourceAsStream("/keys/qica.der");
        final EETClient client = EETServiceFactory.getInstance(clientKey, "eet", serverCertificate, new WebserviceConfiguration(1L));

        final TrzbaDataType data = new TrzbaDataType()
                .withDicPopl("CZ683555118")
                .withIdProvoz(243)
                .withIdPokl("24/A-6/Brno_2")
                .withPoradCis("#135433c/11/2016")
                .withDatTrzby(new Date())
                .withCelkTrzba(new BigDecimal("3264"));

        try {
            client.submitReceipt(data, CommunicationMode.REAL, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
            Assert.fail("Should throw an exception!");
        } catch (final CommunicationException e) {
            final TrzbaType request = e.getRequest();
            Assert.assertNotNull(request);
            Assert.assertNotNull(e.getPKP());
        }
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
