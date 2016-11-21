package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class MerlinWithCRLDistributionPointsExtensionTest {

    private KeyStore productionKeystore;
    private X509Certificate productionCertificate;

    private KeyStore playgroundKeystore;
    private X509Certificate playgroundCertificate;

    @Before
    public void setUp() throws Exception {
        playgroundCertificate = getEETCertificate("/keys/crls-demo-cert.pem");
        playgroundKeystore = getTruststore("/keys/qica.der");

        productionCertificate = getEETCertificate("/keys/crls-prod-cert.pem");
        productionKeystore = getTruststore("/keys/rca15_rsa.der", "/keys/2qca16_rsa.der");
    }


    @Test
    public void verifyTrustPlayground() throws Exception {
        final Merlin crypto = new MerlinWithCRLDistributionPointsExtension();
        final boolean enableRevocation = true;
        final Collection<Pattern> subjectCertConstraints = new ArrayList<Pattern>();
        subjectCertConstraints.add(Pattern.compile(SecureEETCommunication.SUBJECT_CERT_CONSTRAINTS));
        final X509Certificate[] certsPlayground = {playgroundCertificate};
        crypto.setTrustStore(playgroundKeystore);
        crypto.verifyTrust(certsPlayground, enableRevocation,  subjectCertConstraints);
    }

    @Test
    public void verifyTrustProduction() throws WSSecurityException {
        final Merlin crypto = new MerlinWithCRLDistributionPointsExtension();
        final boolean enableRevocation = true;
        final Collection<Pattern> subjectCertConstraints = new ArrayList<Pattern>();
        subjectCertConstraints.add(Pattern.compile(SecureEETCommunication.SUBJECT_CERT_CONSTRAINTS));
        final X509Certificate[] certsProduction = {productionCertificate};
        crypto.setTrustStore(productionKeystore);
        crypto.verifyTrust(certsProduction, enableRevocation,  subjectCertConstraints);
    }



    private KeyStore getTruststore(final String... certificate) throws InvalidKeystoreException {
        final InputStream[] streams = new InputStream[certificate.length];
        for(int i = 0; i< certificate.length; i++) {
            streams[i] = (getClass().getResourceAsStream(certificate[i]));
        }
        return new ServerKey(streams).getTrustStore();
    }

    private X509Certificate getEETCertificate(final String path) throws CertificateException {
        final InputStream is = getClass().getResourceAsStream(path);
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(is);
    }
}