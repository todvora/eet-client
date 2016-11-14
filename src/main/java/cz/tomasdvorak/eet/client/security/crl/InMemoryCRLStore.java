package cz.tomasdvorak.eet.client.security.crl;

import cz.tomasdvorak.eet.client.exceptions.RevocationListException;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class InMemoryCRLStore {

    private static final int CRL_RETRIEVE_TIMEOUT_MILLIS = 2000;

    public static final InMemoryCRLStore INSTANCE = new InMemoryCRLStore();

    private static final Map<URI, X509CRL> CACHE = new HashMap<URI, X509CRL>();

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(InMemoryCRLStore.class);

    public CertStore getCRLStore(final X509Certificate... certificates) throws RevocationListException {
        final List<X509CRL> x509CRLs = new ArrayList<X509CRL>();
        for(final X509Certificate cert : certificates) {
            x509CRLs.addAll(getCrls(cert));
        }
        try {
            return CertStore.getInstance("Collection", new CollectionCertStoreParameters(x509CRLs));
        } catch (final InvalidAlgorithmParameterException e) {
            throw new RevocationListException(e);
        } catch (final NoSuchAlgorithmException e) {
            throw new RevocationListException(e);
        }
    }

    private List<X509CRL> getCrls(final X509Certificate cert) throws RevocationListException {
        final List<X509CRL> result = new ArrayList<X509CRL>();
        final List<URI> uris = CRLUtils.getCRLs(cert);
        final ExecutorService executorService = Executors.newCachedThreadPool();
        final List<Future<X509CRL>> futures = new ArrayList<Future<X509CRL>>();
        for (final URI uri : uris) {
            futures.add(executorService.submit(new Callable<X509CRL>() {
                @Override
                public X509CRL call() throws RevocationListException {
                    return getCRL(uri);
                }
            }));
        }
        for (final Future<X509CRL> future : futures) {
            try {
                final X509CRL x509CRL = future.get(CRL_RETRIEVE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                result.add(x509CRL);
            } catch (final InterruptedException e) {
                throw new RevocationListException(e);
            } catch (final ExecutionException e) {
                throw new RevocationListException(e);
            } catch (final TimeoutException e) {
                throw new RevocationListException(e);
            }
        }
        return result;
    }

    private synchronized X509CRL getCRL(final URI address) throws RevocationListException {
        if(CACHE.containsKey(address)) {
            final X509CRL x509CRL = CACHE.get(address);
            final Date nextUpdate = x509CRL.getNextUpdate();
            if(new Date().before(nextUpdate)) {
                logger.debug("CRL from URI " + address.toString() + " is up-to-date, using cached variant. Next update: " + nextUpdate + ".");
                return x509CRL;
            } else {
                logger.debug("CRL from URI " + address.toString() + " is stale, updating now");
            }
        }
        final X509CRL x509CRL = downloadCRL(address);
        logger.info("CRL loaded from URI " + address.toString() + ", storing in cache. Next update: " + x509CRL.getNextUpdate());
        CACHE.put(address, x509CRL);
        return x509CRL;
    }

    private X509CRL downloadCRL(final URI address) throws RevocationListException {
        try {
            final URL url = new URL(address.toString());
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream inStream = null;
            try  {
                inStream = url.openStream();
                return (X509CRL) cf.generateCRL(inStream);
            } finally {
                if(inStream != null) {
                    inStream.close();
                }
            }
        } catch (final CertificateException e) {
            throw createCrlException(e);
        } catch (final CRLException e) {
            throw createCrlException(e);
        } catch (final IOException e) {
            throw createCrlException(e);
        }
    }

    private RevocationListException createCrlException(final Exception e) {
        return new RevocationListException("Failed to obtain certificate revocation list to be able to validate EET response", e);
    }
}
