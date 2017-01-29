package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.errors.EetErrorType;
import cz.tomasdvorak.eet.client.exceptions.CommunicationException;
import cz.tomasdvorak.eet.client.exceptions.ResponseWithErrorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

@Category(IntegrationTest.class)
public class ErrorRetrievalIntegrationTest {
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
    public void testInvalidTaxNumber() throws Exception {
        final TrzbaDataType data = new TrzbaDataType()
                .withDicPopl("CZ1234567890")
                .withIdProvoz(243)
                .withIdPokl("24/A-6/Brno_2")
                .withPoradCis("#135433c/11/2016")
                .withDatTrzby(new Date())
                .withCelkTrzba(new BigDecimal("3264"));

        final TrzbaType request = eetService.prepareFirstRequest(data, CommunicationMode.TEST);
        try {
            eetService.sendSync(request, EndpointType.PLAYGROUND);
            Assert.fail("Should fail");
        } catch (CommunicationException e) {
            final Throwable cause = e.getCause();
            Assert.assertNotNull(cause);
            Assert.assertEquals(ResponseWithErrorException.class, e.getCause().getClass());
            final ResponseWithErrorException eetError = (ResponseWithErrorException) e.getCause();
            Assert.assertEquals(EetErrorType.INVALID_TAX_NUMBER.getErrorCode(), eetError.getErrorCode());
        }
    }



}
