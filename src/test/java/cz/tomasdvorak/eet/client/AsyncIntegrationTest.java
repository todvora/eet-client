package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.dto.ResponseCallback;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.dto.WebserviceConfiguration;
import cz.tomasdvorak.eet.client.exceptions.CommunicationException;
import cz.tomasdvorak.eet.client.exceptions.CommunicationTimeoutException;
import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import cz.tomasdvorak.eet.client.security.ClientKey;
import cz.tomasdvorak.eet.client.security.ServerKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Category(IntegrationTest.class)
public class AsyncIntegrationTest {


    private EETClient getService(final WebserviceConfiguration configuration) throws InvalidKeystoreException {
        final ClientKey clientKey = ClientKey.fromInputStream(getClass().getResourceAsStream("/keys/CZ683555118.p12"), "eet");
        final ServerKey serverKey = ServerKey.trustingEmbeddedCertificates();
        return EETServiceFactory.getInstance(clientKey, serverKey, configuration);
    }


    @Test(timeout = 10000)
    public void testAsyncCommunication() throws Exception {

        final EETClient eetClient = getService(WebserviceConfiguration.DEFAULT);

        final int requestsCount = 10;
        final CountDownLatch lock = new CountDownLatch(requestsCount);
        final List<SubmitResult> results = Collections.synchronizedList(new ArrayList<SubmitResult>());
        final List<CommunicationException> errors = Collections.synchronizedList(new ArrayList<CommunicationException>());

        for(int i = 0; i< requestsCount; i++) {
            final TrzbaDataType data = getData(i);
            final TrzbaType request = eetClient.prepareFirstRequest(data, CommunicationMode.REAL);
            eetClient.sendAsync(request, EndpointType.PLAYGROUND, new ResponseCallback() {
                @Override
                public void onComplete(final SubmitResult result) {
                    results.add(result);
                    lock.countDown();
                }
                @Override
                public void onError(final CommunicationException cause) {
                    errors.add(cause);
                    lock.countDown();
                }

                @Override
                public void onTimeout(final CommunicationTimeoutException cause) {
                    errors.add(cause);
                    lock.countDown();
                }
            });
        }
        lock.await();
        Assert.assertEquals(requestsCount, results.size());

        for(final SubmitResult result : results) {
            Assert.assertNotNull(result.getFik());
        }
    }

    @Test(timeout = 10000)
    public void testAsyncCommunicationTimeout() throws Exception {
            final TrzbaDataType data = getData(1);

        final EETClient eetClient = getService(new WebserviceConfiguration(1L)); // one millisecond timeout!
        final TrzbaType request = eetClient.prepareFirstRequest(data, CommunicationMode.REAL);
        eetClient.sendAsync(request, EndpointType.PLAYGROUND, new ResponseCallback() {
                @Override
                public void onComplete(final SubmitResult result) {
                    Assert.fail("Should be handled in onError method");
                }
                @Override
                public void onError(final CommunicationException cause) {
                    Assert.fail("Should be handled in onTimeout");
                }

                @Override
                public void onTimeout(final CommunicationTimeoutException cause) {
                    Assert.assertNotNull(cause.getPKP());
                }
            });
    }

    private TrzbaDataType getData(final int receiptNumber) {
        return new TrzbaDataType()
                .withDicPopl("CZ683555118")
                .withIdProvoz(243)
                .withIdPokl("24/A-6/Brno_2")
                .withPoradCis("" + receiptNumber)
                .withDatTrzby(new Date())
                .withCelkTrzba(new BigDecimal("3264"));
    }


}
