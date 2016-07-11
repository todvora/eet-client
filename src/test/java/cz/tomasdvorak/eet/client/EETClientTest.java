package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.OdpovedType;
import cz.etrzby.xml.TrzbaDataType;
import cz.tomasdvorak.eet.client.dto.CommunicationMode;
import cz.tomasdvorak.eet.client.dto.EndpointType;
import cz.tomasdvorak.eet.client.dto.SubmissionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Category(IntegrationTest.class)
public class EETClientTest {

    private EETClient eetService;

    @Before
    public void setUp() throws Exception {
        final InputStream clientKey = getClass().getResourceAsStream("/keys/01000005.p12");
        final InputStream serverCertificate = getClass().getResourceAsStream("/keys/qica.der");
        final InputStream crl = getClass().getResourceAsStream("/keys/revocated.crl");
        this.eetService = EETServiceFactory.getInstance(clientKey, "eet", serverCertificate, crl);
    }

    @Test
    public void realCommunication() throws Exception {
        final TrzbaDataType data = getData();
        final OdpovedType odpovedType = eetService.submitReceipt(data, CommunicationMode.REAL, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
        Assert.assertNotNull(odpovedType.getPotvrzeni().getFik());
        Assert.assertNull(odpovedType.getChyba());
    }

    @Test
    public void testCommunication() throws Exception {
        final TrzbaDataType data = getData();
        final OdpovedType odpovedType = eetService.submitReceipt(data, CommunicationMode.TEST, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
        Assert.assertNull(odpovedType.getPotvrzeni());
        Assert.assertEquals("Datovou zpravu evidovane trzby v overovacim modu se podarilo zpracovat", odpovedType.getChyba().getContent());
        Assert.assertEquals(0, odpovedType.getChyba().getKod());
    }

    private TrzbaDataType getData() {
        return new TrzbaDataType()
            .withDicPopl("CZ683555118")
            .withIdProvoz(243)
            .withIdPokl("24/A-6/Brno_2")
            .withPoradCis("#135433c/11/2016")
            .withDatTrzby(new Date())
            .withCelkTrzba(new BigDecimal("3264.00"));
    }
}