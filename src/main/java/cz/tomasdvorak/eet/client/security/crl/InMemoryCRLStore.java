package cz.tomasdvorak.eet.client.security.crl;

import cz.tomasdvorak.eet.client.exceptions.RevocationListException;
import cz.tomasdvorak.eet.client.security.ServerKey;
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
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCRLStore {

    public static final InMemoryCRLStore INSTANCE = new InMemoryCRLStore();

    private static final Map<URI, X509CRL> CACHE = new ConcurrentHashMap<>();

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ServerKey.class);

    public CertStore getCRLStore(final X509Certificate[] certificates) throws RevocationListException {
        final List<X509CRL> x509CRLs = new ArrayList<>();
        for(final X509Certificate cert : certificates) {
            final List<URI> uris = CRLUtils.getCRLs(cert);
            for(final URI uri : uris) {
                x509CRLs.add(getCRL(uri));
            }
        }
        try {
            return CertStore.getInstance("Collection", new CollectionCertStoreParameters(x509CRLs));
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            throw new RevocationListException(e);
        }
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
        final X509CRL x509CRL = loadCRL(address);
        logger.info("CRL loaded from URI " + address.toString() + ", storing in cache. Next update: " + x509CRL.getNextUpdate());
        CACHE.put(address, x509CRL);
        return x509CRL;
    }

    private X509CRL loadCRL(final URI address) throws RevocationListException {
        try {
            final URL url = new URL(address.toString());
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            try (final InputStream inStream = url.openStream()) {
                return (X509CRL) cf.generateCRL(inStream);
            }
        } catch (CertificateException | CRLException | IOException e) {
            throw new RevocationListException(e);
        }
    }
}
