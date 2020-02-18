package cz.tomasdvorak.eet.client.security;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;

public class MerlinWithCRLDistributionPointsExtensionTest {

    private KeyStore keystore;
    private X509Certificate productionCertificate;
    private X509Certificate playgroundCertificate;

    @Before
    public void setUp() throws Exception {
        playgroundCertificate = getEETCertificate("/keys/crls-demo-cert.pem");
        productionCertificate = getEETCertificate("/keys/crls-prod-cert.pem");
        keystore = getTruststore("/certificates/rca15_rsa.der", "/certificates/2qca16_rsa.der");
    }

    @Ignore("Disabled due to expired test certificate as well")
    @Test
    public void verifyTrustPlayground() throws Exception {
        final Merlin crypto = new MerlinWithCRLDistributionPointsExtension();
        final boolean enableRevocation = true;
        final Collection<Pattern> subjectCertConstraints = new ArrayList<Pattern>();
        subjectCertConstraints.add(Pattern.compile(SecureEETCommunication.SUBJECT_CERT_CONSTRAINTS));
        final X509Certificate[] certsPlayground = { playgroundCertificate };
        crypto.setTrustStore(keystore);
        crypto.verifyTrust(certsPlayground, enableRevocation, subjectCertConstraints, null);
    }

    @Ignore("Disabled due to expired production certificate. Help needed, please see https://github.com/todvora/eet-client/issues/35#issuecomment-340262163")
    @Test
    public void verifyTrustProduction() throws WSSecurityException {
        final Merlin crypto = new MerlinWithCRLDistributionPointsExtension();
        final boolean enableRevocation = true;
        final Collection<Pattern> subjectCertConstraints = new ArrayList<Pattern>();
        subjectCertConstraints.add(Pattern.compile(SecureEETCommunication.SUBJECT_CERT_CONSTRAINTS));
        final X509Certificate[] certsProduction = { productionCertificate };
        crypto.setTrustStore(keystore);
        crypto.verifyTrust(certsProduction, enableRevocation, subjectCertConstraints, null);
    }

    private KeyStore getTruststore(final String... certificate) throws InvalidKeystoreException {
        final InputStream[] streams = new InputStream[certificate.length];
        for (int i = 0; i < certificate.length; i++) {
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
