package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Keystore (=truststore) holding root certificate of the CA used to create server's keys. The server public certificate
 * will be validated against the root CA (currently I.CA, not contained in the default java keystore).
 */
public class ServerKey {
    private final KeyStore truststore;
    private final CertStore revocated;

    public ServerKey(final InputStream caCertificate, final InputStream certificateRevocationList) throws InvalidKeystoreException {
        try {
            this.truststore = keystoreOf(caCertificate);
            this.revocated = certstoreOf(certificateRevocationList);

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
        final Certificate certificate = cf.generateCertificate(caCertificate);
        ks.setCertificateEntry("ICA", certificate);
        return ks;
    }

    public Crypto getCrypto() {
        final Merlin merlin = new Merlin();
        merlin.setTrustStore(this.truststore);
        merlin.setCRLCertStore(this.revocated);
        return merlin;
    }

    KeyStore getTruststore() {
        return truststore;
    }
}
