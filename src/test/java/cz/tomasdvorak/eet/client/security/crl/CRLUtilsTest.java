package cz.tomasdvorak.eet.client.security.crl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CRLUtilsTest {

    private X509Certificate certificate;

    @Before
    public void setUp() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/keys/crls-demo-cert.pem");
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        certificate = (X509Certificate) certificateFactory.generateCertificate(is);
    }

    @Test
    public void getCRLs() throws Exception {
        final List<String> actual = CRLUtils.getCRLs(certificate)
                .stream()
                .map(URI::toString)
                .collect(Collectors.toList());

        final List<String> expected = Arrays.asList(
                "http://qcrldp1.ica.cz/qica09.crl",
                "http://qcrldp2.ica.cz/qica09.crl",
                "http://qcrldp3.ica.cz/qica09.crl");

        Assert.assertEquals("Failed to read all CRLs from a certificate", expected, actual);
    }

}