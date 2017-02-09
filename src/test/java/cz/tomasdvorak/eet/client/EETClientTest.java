package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.dto.WebserviceConfiguration;
import cz.tomasdvorak.eet.client.exceptions.CommunicationTimeoutException;
import cz.tomasdvorak.eet.client.security.ClientKey;
import cz.tomasdvorak.eet.client.security.ServerKey;
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
        final ClientKey clientKey = ClientKey.fromInputStream(getClass().getResourceAsStream("/keys/CZ683555118.p12"), "eet");
        final ServerKey serverKey = ServerKey.trustingEmbeddedCertificates();
        this.eetService = EETServiceFactory.getInstance(clientKey, serverKey);
    }

    @Test
    public void realCommunication() throws Exception {
        final TrzbaDataType data = getData();
        final TrzbaType request = eetService.prepareFirstRequest(data, CommunicationMode.REAL);
        final SubmitResult result = eetService.sendSync(request, EndpointType.PLAYGROUND);
        Assert.assertNull(result.getChyba());
        Assert.assertNotNull(result.getFik());
        final String bkpFromRequest = result.getBKP();
        final String bkpFromResponse = result.getHlavicka().getBkp();
        Assert.assertEquals(bkpFromRequest, bkpFromResponse);
    }

    @Test
    public void testCommunication() throws Exception {
        final TrzbaDataType receipt = getData();
        final TrzbaType request = eetService.prepareFirstRequest(receipt, CommunicationMode.TEST);
        final SubmitResult result = eetService.sendSync(request, EndpointType.PLAYGROUND);
        Assert.assertNull(result.getPotvrzeni());
        Assert.assertEquals("Datovou zpravu evidovane trzby v overovacim modu se podarilo zpracovat", result.getChyba().getContent());
        Assert.assertEquals(0, result.getChyba().getKod());
    }

    @Test
    public void testTimeoutHandling() throws Exception {
        final ClientKey clientKey = ClientKey.fromInputStream(getClass().getResourceAsStream("/keys/CZ683555118.p12"), "eet");
        final ServerKey serverCertificate = ServerKey.trustingEmbeddedCertificates();
        final EETClient client = EETServiceFactory.getInstance(clientKey, serverCertificate, new WebserviceConfiguration(1));

        final TrzbaDataType data = new TrzbaDataType()
                .withDicPopl("CZ683555118")
                .withIdProvoz(243)
                .withIdPokl("24/A-6/Brno_2")
                .withPoradCis("#135433c/11/2016")
                .withDatTrzby(new Date())
                .withCelkTrzba(new BigDecimal("3264"));

        try {
            final TrzbaType request = client.prepareFirstRequest(data, CommunicationMode.REAL);
            client.sendSync(request, EndpointType.PLAYGROUND);
            Assert.fail("Should throw an exception!");
        } catch (final CommunicationTimeoutException e) {
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
            .withDatTrzby(new Date())
            .withCelkTrzba(new BigDecimal("3264"));
    }
}
