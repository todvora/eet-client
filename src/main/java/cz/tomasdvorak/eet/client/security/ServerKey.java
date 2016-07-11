package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class ServerKey {
    private final KeyStore truststore;

    public ServerKey(final InputStream caCertificate) throws InvalidKeystoreException {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = "changeit".toCharArray();
            ks.load(null, password);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final java.security.cert.Certificate certificate = cf.generateCertificate(caCertificate);
            ks.setCertificateEntry("ICA", certificate);
            this.truststore = ks;
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            throw new InvalidKeystoreException(e);
        }
    }

    public Crypto getCrypto() {
        final Merlin merlin = new Merlin();
        merlin.setKeyStore(this.truststore);
        return merlin;
    }

    KeyStore getTruststore() {
        return truststore;
    }
}
