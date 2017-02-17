package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.config.SubmissionType;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.dto.WebserviceConfiguration;
import cz.tomasdvorak.eet.client.exceptions.CommunicationException;
import cz.tomasdvorak.eet.client.exceptions.CommunicationTimeoutException;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

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
        final String bkpFromRequest = result.getBKP();
        final String bkpFromResponse = result.getHlavicka().getBkp();
        Assert.assertEquals(bkpFromRequest, bkpFromResponse);
    }

    @Test
    public void testInvalidResponseSignature() throws Exception {
        final InputStream clientKey = getClass().getResourceAsStream("/keys/CZ683555118.p12");
        final InputStream serverCertificate = getClass().getResourceAsStream("/keys/2qca16_rsa.der"); // This CA is not valid for playground, should throw an Exception
        final EETClient client = EETServiceFactory.getInstance(clientKey, "eet", serverCertificate);
        try {
            client.submitReceipt(getData(), CommunicationMode.REAL, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
            Assert.fail("Should fail due to error during certificate path validation");
        } catch (CommunicationException e) {
            final Throwable securityException = e.getCause().getCause();
            Assert.assertEquals(WSSecurityException.class, securityException.getClass());
            Assert.assertEquals("Error during certificate path validation: No trusted certs found", securityException.getMessage());
        }
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
        final EETClient client = EETServiceFactory.getInstance(new WebserviceConfiguration(1), clientKey, "eet", serverCertificate);

        final TrzbaDataType data = new TrzbaDataType()
                .withDicPopl("CZ683555118")
                .withIdProvoz(243)
                .withIdPokl("24/A-6/Brno_2")
                .withPoradCis("#135433c/11/2016")
                .withDatTrzby(ZonedDateTime.now())
                .withCelkTrzba(new BigDecimal("3264"));

        try {
            client.submitReceipt(data, CommunicationMode.REAL, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
            Assert.fail("Should throw an exception!");
        } catch (final CommunicationTimeoutException e) {
            System.out.println("Timeout");
            final TrzbaType request = e.getRequest();
            Assert.assertNotNull(request);
            Assert.assertNotNull(e.getPKP());
            Assert.assertNotNull(e.getBKP());
        }
    }

    private TrzbaDataType getData() {
        return new TrzbaDataType()
                .withDicPopl("CZ683555118")
                .withIdProvoz(243)
                .withIdPokl("24/A-6/Brno_2")
                .withPoradCis("#135433c/11/2016")
                .withDatTrzby(ZonedDateTime.now())
                .withCelkTrzba(new BigDecimal("3264"));
    }
}
