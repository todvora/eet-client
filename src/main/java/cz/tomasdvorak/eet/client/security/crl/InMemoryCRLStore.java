package cz.tomasdvorak.eet.client.security.crl;

import cz.tomasdvorak.eet.client.exceptions.RevocationListException;
import org.apache.logging.log4j.LogManager;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * CertStore created on the fly, containing all the CRLs obtained from an {@link X509Certificate certificate instance}.
 * The CRLs are downloaded and persisted in memory, respecting the nextUpdate time. This makes validation of second and every other
 * response signed with the same cert much faster, without the need of re-downloading CRLs again.
 *
 * CLRs are downloaded in parallel and the code is thread safe, synchronized.
 */
public class InMemoryCRLStore {

    private static final int CRL_RETRIEVE_TIMEOUT_SECONDS = 2;

    public static final InMemoryCRLStore INSTANCE = new InMemoryCRLStore();

    private static final Map<URI, X509CRL> CACHE = new ConcurrentHashMap<URI, X509CRL>();

    private static final Logger logger = LogManager.getLogger(InMemoryCRLStore.class);

    public synchronized CertStore getCRLStore(final X509Certificate... certificates) throws RevocationListException {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            final List<Callable<X509CRL>> x509CRLs = new ArrayList<Callable<X509CRL>>();
            for (final X509Certificate cert : certificates) {
                x509CRLs.addAll(getCrls(cert));
            }
            final List<Future<X509CRL>> futures = executorService.invokeAll(x509CRLs, CRL_RETRIEVE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            final List<X509CRL> crls = new ArrayList<X509CRL>();
            for (Future<X509CRL> future : futures) {
                crls.add(future.get());
            }
            return CertStore.getInstance("Collection", new CollectionCertStoreParameters(crls));
        } catch (final InvalidAlgorithmParameterException e) {
            throw new RevocationListException(e);
        } catch (final NoSuchAlgorithmException e) {
            throw new RevocationListException(e);
        } catch (InterruptedException e) {
            throw new RevocationListException(e);
        } catch (ExecutionException e) {
            throw new RevocationListException(e);
        } finally {
            executorService.shutdownNow();
        }
    }

    private List<Callable<X509CRL>> getCrls(final X509Certificate cert) throws RevocationListException {
        final List<URI> uris = CRLUtils.getCRLs(cert);
        final List<Callable<X509CRL>> callables = new ArrayList<Callable<X509CRL>>();
        for (final URI uri : uris) {
            callables.add(new Callable<X509CRL>() {
                @Override
                public X509CRL call() throws RevocationListException {
                    return getCRL(uri);
                }
            });
        }
        return callables;
    }

    private X509CRL getCRL(final URI address) throws RevocationListException {
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
