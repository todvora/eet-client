package cz.tomasdvorak.eet.client.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateUtilsTest {

    @Test
    public void testServerKeyInfo() throws Exception {
        final X509Certificate x509Certificate = getEETCertificate("/keys/crls-demo-cert.pem");
        Assert.assertEquals("{subject='SERIALNUMBER=ICA - 10374619, O=Česká republika - Generální finanční ředitelství, CN=Elektronická evidence tržeb - Playground, C=CZ', issuer='OU=I.CA - Accredited Provider of Certification Services, O=\"První certifikační autorita, a.s.\", CN=\"I.CA - Qualified Certification Authority, 09/2009\", C=CZ', SerialNumber=11140368, validFrom=2016-06-08T07:54:52+02:00, validTo=2017-06-08T07:54:52+02:00}", CertificateUtils.getCertificateInfo(x509Certificate));

    }

    @Test
    public void getClientKeyInfo() throws Exception {
        final KeyStore keystore = KeyStore.getInstance("pkcs12");
        final InputStream inputStream = getClass().getResourceAsStream("/keys/CZ1212121218.p12");
        keystore.load(inputStream, "eet".toCharArray());
        inputStream.close();
        final String certificateInfo = CertificateUtils.getCertificateInfo(keystore, "1");
        Assert.assertEquals("using alias=1: client keyEntry: {subject='OID.2.5.4.13=fyzicka osoba, CN=CZ1212121218, DC=CZ', issuer='CN=EET CA 1 Playground, O=Česká Republika – Generální finanční ředitelství, DC=CZ', SerialNumber=1446418224, validFrom=2016-09-30T11:02:44+02:00, validTo=2019-09-30T11:02:44+02:00}", certificateInfo);

    }

    private X509Certificate getEETCertificate(final String path) throws CertificateException {
        final InputStream is = getClass().getResourceAsStream(path);
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(is);
    }

}