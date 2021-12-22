package cz.tomasdvorak.eet.client.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class CertExpirationCheckerTest {

    @Test
    public void fromKeystoreExpiresSoon() throws Exception {

        Logger mock = Mockito.mock(Logger.class);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        final KeyStore keystore = getKeyStore("/keys/CZ1212121218.p12");
        Date now = DateUtils.parse("2019-09-12T11:02:44+02:00");

        CertExpirationChecker.of(keystore, "1")
                .withCompareAgainstDate(now)
                .whenExpiresIn(20, TimeUnit.DAYS)
                .printWarningTo(mock);

        final String expected = "\n" +
                "#### WARNING ####\n" +
                "Following certificate expires on 2019-09-30T11:02:44+02:00!\n" +
                "{subject='OID.2.5.4.13=fyzicka osoba, CN=CZ1212121218, DC=CZ', issuer='CN=EET CA 1 Playground, O=Česká Republika – Generální finanční ředitelství, DC=CZ', SerialNumber=1446418224, validFrom=2016-09-30T11:02:44+02:00, validTo=2019-09-30T11:02:44+02:00}\n" +
                "Please update your certificate as soon as possible. More info on https://github.com/todvora/eet-client#certificate-expiration\n" +
                "##################";

        Mockito.verify(mock, Mockito.times(1)).warn(captor.capture());
        Assert.assertEquals(expected, captor.getValue());
    }

    @Test
    public void fromKeystoreNotExpired() throws Exception {

        Logger mock = Mockito.mock(Logger.class);

        final KeyStore keystore = getKeyStore("/keys/CZ1212121218.p12");
        Date now = DateUtils.parse("2019-09-12T11:02:44+02:00");

        CertExpirationChecker.of(keystore, "1")
                .withCompareAgainstDate(now)
                .whenExpiresIn(15, TimeUnit.DAYS)
                .printWarningTo(mock);

        Mockito.verify(mock, Mockito.never()).warn(Mockito.anyString());
    }

    private KeyStore getKeyStore(String name) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        final KeyStore keystore = KeyStore.getInstance("pkcs12");
        final InputStream inputStream = getClass().getResourceAsStream(name);
        keystore.load(inputStream, "eet".toCharArray());
        inputStream.close();
        return keystore;
    }

    @Test
    public void fromCertificateExpiresSoon() throws Exception {
        Logger mock = Mockito.mock(Logger.class);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        final X509Certificate serverCertificate = getCertificate("/certificates/2qca16_rsa.der");
        Date now = DateUtils.parse("2026-02-01T13:17:11+01:00");

        CertExpirationChecker.of(serverCertificate)
                .withCompareAgainstDate(now)
                .whenExpiresIn(20, TimeUnit.DAYS)
                .printWarningTo(mock);

        final String expected = "\n#### WARNING ####\n" +
                "Following certificate expires on 2026-02-08T13:17:11+01:00!\n" +
                "{subject='SERIALNUMBER=NTRCZ-26439395, O=\"První certifikační autorita, a.s.\", CN=I.CA Qualified 2 CA/RSA 02/2016, C=CZ', issuer='SERIALNUMBER=NTRCZ-26439395, CN=I.CA Root CA/RSA, O=\"První certifikační autorita, a.s.\", C=CZ', SerialNumber=100001006, validFrom=2016-02-11T13:17:11+01:00, validTo=2026-02-08T13:17:11+01:00}\n" +
                "Please update your certificate as soon as possible. More info on https://github.com/todvora/eet-client#certificate-expiration\n" +
                "##################";

        Mockito.verify(mock, Mockito.times(1)).warn(captor.capture());
        Assert.assertEquals(expected, captor.getValue());
    }

    @Test
    public void fromCertificateNotExpired() throws Exception {
        Logger mock = Mockito.mock(Logger.class);

        final X509Certificate serverCertificate = getCertificate("/certificates/2qca16_rsa.der");
        Date now = DateUtils.parse("2019-08-15T07:00:00+02:00");

        CertExpirationChecker.of(serverCertificate)
                .withCompareAgainstDate(now)
                .whenExpiresIn(15, TimeUnit.DAYS)
                .printWarningTo(mock);

        Mockito.verify(mock, Mockito.never()).warn(Mockito.anyString());
    }

    private X509Certificate getCertificate(final String path) throws CertificateException {
        final InputStream is = getClass().getResourceAsStream(path);
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(is);
    }

}