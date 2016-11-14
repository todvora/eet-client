package cz.tomasdvorak.eet.client.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateUtilsTest {

    @Test
    public void testServerKeyInfo() throws Exception {
        final X509Certificate x509Certificate = getEETCertificate("/keys/crls-demo-cert.pem");
        Assert.assertEquals("{subject='SERIALNUMBER=ICA - 10374619, O=Česká republika - Generální finanční ředitelství, CN=Elektronická evidence tržeb - Playground, C=CZ', issuer='OU=I.CA - Accredited Provider of Certification Services, O=\"První certifikační autorita, a.s.\", CN=\"I.CA - Qualified Certification Authority, 09/2009\", C=CZ', SerialNumber=11140368, validFrom=2016-06-08T07:54:52+02:00, validTo=2017-06-08T07:54:52+02:00}", CertificateUtils.getCertificateInfo(x509Certificate));

    }

    private X509Certificate getEETCertificate(final String path) throws CertificateException {
        final InputStream is = getClass().getResourceAsStream(path);
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(is);
    }

}