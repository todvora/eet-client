package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.apache.logging.log4j.Logger;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.Collections;

/**
 * Keystore (=trustStore) holding root certificate of the CA used to create server's keys. The server public certificate
 * will be validated against the root CA (currently I.CA, not contained in the default java keystore).
 */
public class ServerKey {

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ServerKey.class);

    protected static final String KEY_ALIAS = "SERVER";

    private final KeyStore trustStore;
    private final CertStore crlCertStore;

    public ServerKey(final InputStream caCertificate, final InputStream certificateRevocationList) throws InvalidKeystoreException {
        try {
            this.trustStore = keystoreOf(caCertificate);
            this.crlCertStore = certstoreOf(certificateRevocationList);

        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | CRLException | InvalidAlgorithmParameterException e) {
            throw new InvalidKeystoreException(e);
        }
    }

    private CertStore certstoreOf(final InputStream crlStream) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, CRLException, InvalidAlgorithmParameterException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL x509crl = (X509CRL) cf.generateCRL(crlStream);
        return CertStore.getInstance("Collection", new CollectionCertStoreParameters(Collections.singletonList(x509crl))
        );
    }

    private KeyStore keystoreOf(final InputStream caCertificate) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final X509Certificate certificate = (X509Certificate) cf.generateCertificate(caCertificate);

        logger.info("Server certificate serial number: " + certificate.getSerialNumber() + ", " + certificate.getIssuerDN().toString());

        ks.setCertificateEntry(KEY_ALIAS, certificate);
        return ks;
    }

    public Crypto getCrypto() {
        final Merlin merlin = new Merlin();
        merlin.setTrustStore(this.trustStore);
        merlin.setCRLCertStore(this.crlCertStore);
        return merlin;
    }

    KeyStore getTrustStore() {
        return trustStore;
    }
}
