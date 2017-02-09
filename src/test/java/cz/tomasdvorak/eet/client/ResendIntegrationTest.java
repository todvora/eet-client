package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.dto.WebserviceConfiguration;
import cz.tomasdvorak.eet.client.exceptions.CommunicationTimeoutException;
import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import cz.tomasdvorak.eet.client.persistence.RequestSerializer;
import cz.tomasdvorak.eet.client.security.ClientKey;
import cz.tomasdvorak.eet.client.security.ServerKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

@Category(IntegrationTest.class)
public class ResendIntegrationTest {

    @Test
    public void testResendAfterFailure() throws Exception {
        // will be used for first submission, returns with timeout.
        final EETClient timeoutingService = EETServiceFactory.getInstance(getClientKey(), ServerKey.trustingEmbeddedCertificates(), new WebserviceConfiguration(1));
        final EETClient functionalService = EETServiceFactory.getInstance(getClientKey(), ServerKey.trustingEmbeddedCertificates());
        TrzbaType request;
        String persistedRequest = null;

        try {
            // prepare request, compute BKP and PKP kodes, fill header
            request = timeoutingService.prepareFirstRequest(getData(), CommunicationMode.REAL);

            // persist, just in case. This request will be used later for repeated submission
            persistedRequest = RequestSerializer.toString(request);

            // try to send for first time
            timeoutingService.sendSync(request, EndpointType.PLAYGROUND);
            Assert.fail("Should throw an exception!");
        } catch (final CommunicationTimeoutException e) {
            // timeouted, this is expected.
        }

        // restore request from persisted state
        final TrzbaType restoredRequest = RequestSerializer.fromString(persistedRequest);

        // refresh UUID, send date, compute new security codes, if needed
        final TrzbaType toResend = functionalService.prepareRepeatedRequest(restoredRequest);

        // send refreshed request
        final SubmitResult result = functionalService.sendSync(toResend, EndpointType.PLAYGROUND);

        // validate that send succeeded.
        Assert.assertNull(result.getChyba());
        Assert.assertNotNull(result.getFik());
        Assert.assertNotNull(result.getBKP());
    }

    private ClientKey getClientKey() throws InvalidKeystoreException {
        return ClientKey.fromInputStream(getClass().getResourceAsStream("/keys/CZ683555118.p12"), "eet");
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
