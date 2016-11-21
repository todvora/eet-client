package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import cz.tomasdvorak.eet.client.utils.CertificateUtils;
import cz.tomasdvorak.eet.client.utils.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Keystore (=trustStore) holding root certificate of the CA used to create server's keys. The server public certificate
 * will be validated against the root CA (currently I.CA, not contained in the default java keystore).
 */
public class ServerKey {

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ServerKey.class);

    private final KeyStore trustStore;

    /**
     * Create new ServerKey instance based on data provided in the streams
     * @param streams will be closed automatically
     */
    public ServerKey(final InputStream... streams) throws InvalidKeystoreException {
        try {
            this.trustStore = keystoreOf(Arrays.asList(streams));
        } catch (final CertificateException e) {
            throw new InvalidKeystoreException(e);
        } catch (final NoSuchAlgorithmException e) {
            throw new InvalidKeystoreException(e);
        } catch (final KeyStoreException e) {
            throw new InvalidKeystoreException(e);
        } catch (final IOException e) {
            throw new InvalidKeystoreException(e);
        }
    }

    private KeyStore keystoreOf(final Collection<InputStream> certificates) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, InvalidKeystoreException {
        final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        for(final InputStream cert : certificates) {
            if(cert == null) {
                throw new InvalidKeystoreException("Input stream of ServerKey cannot be NULL");
            }
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                final X509Certificate certificate = (X509Certificate) cf.generateCertificate(cert);
                logger.info("Server certificate: " + CertificateUtils.getCertificateInfo(certificate));
                ks.setCertificateEntry(certificate.getSubjectDN().toString(), certificate);
                cert.close();
            } finally {
                IOUtils.closeQuietly(cert);
            }
        }
        return ks;
    }

    public Crypto getCrypto() {
        final Merlin merlin = new MerlinWithCRLDistributionPointsExtension();
        merlin.setTrustStore(this.trustStore);
        return merlin;
    }

    KeyStore getTrustStore() {
        return trustStore;
    }
}
