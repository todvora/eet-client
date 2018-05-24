package cz.tomasdvorak.eet.client.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CertificateUtilsTest {


    @Test
    public void testServerKeyInfo() throws Exception {
        final X509Certificate x509Certificate = getCertificate("/keys/crls-demo-cert.pem");
        Assert.assertEquals("{subject='SERIALNUMBER=ICA - 10461704, OID.2.5.4.97=NTRCZ-72080043, O=Česká republika - Generální finanční ředitelství, C=CZ, CN=Elektronická evidence tržeb - Playground', issuer='SERIALNUMBER=NTRCZ-26439395, O=\"První certifikační autorita, a.s.\", CN=I.CA Qualified 2 CA/RSA 02/2016, C=CZ', SerialNumber=11390220, validFrom=2018-05-24T10:42:21+02:00, validTo=2019-05-24T10:42:21+02:00}", CertificateUtils.getCertificateInfo(x509Certificate));

    }

    @Test
    public void getClientKeyInfo() throws Exception {
        final KeyStore keystore = KeyStore.getInstance("pkcs12", new BouncyCastleProvider());
        final InputStream inputStream = getClass().getResourceAsStream("/keys/CZ1212121218.p12");
        keystore.load(inputStream, "eet".toCharArray());
        inputStream.close();
        final String certificateInfo = CertificateUtils.getCertificateInfo(keystore, "79a6e8cf65cf6ed2d7b5f3b49f539cf576bee0f6");
        Assert.assertEquals("using alias=79a6e8cf65cf6ed2d7b5f3b49f539cf576bee0f6: client keyEntry: {subject='DC=CZ,CN=CZ1212121218,2.5.4.13=fyzicka osoba', issuer='DC=CZ,O=Česká Republika – Generální finanční ředitelství,CN=EET CA 1 Playground', SerialNumber=1446418224, validFrom=2016-09-30T11:02:44+02:00, validTo=2019-09-30T11:02:44+02:00}", certificateInfo);

    }

    private X509Certificate getCertificate(final String path) throws CertificateException {
        final InputStream is = getClass().getResourceAsStream(path);
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(is);
    }

}