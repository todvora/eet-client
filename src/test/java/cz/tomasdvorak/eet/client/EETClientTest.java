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

@Category(IntegrationTest.class)
public class EETClientTest {

    private EETClient eetService;

    @Before
    public void setUp() throws Exception {
        final InputStream keystore = getClass().getResourceAsStream("/keys/01000005.p12");
        eetService = EETServiceFactory.getInstance(keystore, "eet");
    }

    @Test
    public void notifyReceipt() throws Exception {
        final TrzbaDataType data = new TrzbaDataType();
        data.setDicPopl("CZ683555118");
        data.setIdProvoz(243);
        data.setIdPokl("24/A-6/Brno_2");
        data.setPoradCis("#135433c/11/2016");
        data.setDatTrzby(new Date());
        data.setCelkTrzba(new BigDecimal("3264.00"));

        final OdpovedType odpovedType = eetService.submitReceipt(data, CommunicationMode.TEST, EndpointType.PLAYGROUND, SubmissionType.FIRST_ATTEMPT);
        Assert.assertEquals(0, odpovedType.getChyba().getKod());
        Assert.assertEquals("Datovou zpravu evidovane trzby v overovacim modu se podarilo zpracovat", odpovedType.getChyba().getContent());
    }
}