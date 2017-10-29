package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import cz.tomasdvorak.eet.client.utils.CertExpirationChecker;
import cz.tomasdvorak.eet.client.utils.CertificateUtils;
import cz.tomasdvorak.eet.client.utils.IOUtils;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Keystore (=trustStore) holding root certificate of the CA used to create server's keys. The server public certificate
 * will be validated against the root CA (currently I.CA, not contained in the default java keystore).
 */
public class ServerKey {

    private static final Logger logger = LoggerFactory.getLogger(ServerKey.class);

    private final KeyStore trustStore;

    /**
     * Create new ServerKey instance based on data provided in the streams
     * @deprecated use {@link #fromInputStream(InputStream...)} instead.
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

    /**
     * Create a server key from file path (may be relative or absolute)
     */
    public static ServerKey fromFile(final String filePath) throws InvalidKeystoreException {
        try {
            return new ServerKey(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            throw new InvalidKeystoreException(e);
        }
    }

    public static ServerKey fromInputStream(final InputStream... streams) throws InvalidKeystoreException {
        return new ServerKey(streams);
    }

    /**
     * Warning! This method will supply you a set of trusted certificate authorities, which are distributed together
     * with the client implementation. They can expire, get revoked or get changed without prior notice!
     * @return ServerKey instance initialized with all playground and production certificate authority keys
     * @throws InvalidKeystoreException
     */
    public static ServerKey trustingEmbeddedCertificates() throws InvalidKeystoreException {
        return new ServerKey(
                ServerKey.class.getClassLoader().getResourceAsStream("certificates/2qca16_rsa.der"), // production intermediate CA
                ServerKey.class.getClassLoader().getResourceAsStream("certificates/rca15_rsa.der") // production root CA
        );
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
                CertExpirationChecker.of(certificate)
                        .whenExpiresIn(30, TimeUnit.DAYS)
                        .printWarningTo(logger);
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
