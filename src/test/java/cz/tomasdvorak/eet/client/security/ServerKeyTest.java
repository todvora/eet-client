package cz.tomasdvorak.eet.client.security;


import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.*;

public class ServerKeyTest {

    private KeyStore productionKeystore;
    private X509Certificate productionCertificate;

    private KeyStore playgroundKeystore;
    private X509Certificate playgroundCertificate;

    @Before
    public void setUp() throws Exception {
        productionCertificate = getEETCertificate("/keys/crls-prod-cert.pem");
        playgroundCertificate = getEETCertificate("/keys/crls-demo-cert.pem");
        productionKeystore = getTruststore("/keys/2qca16_rsa.der");
        playgroundKeystore = getTruststore("/keys/qica.der");
    }

    @Test
    public void testImport() throws Exception {
        hasOneAlias(productionKeystore);
//        hasOneAlias(playgroundKeystore);
    }

    @Test
    public void testTrust() throws Exception {
        isTrusted(productionCertificate, productionKeystore);
//        isTrusted(playgroundCertificate, playgroundKeystore);

    }

    private void hasOneAlias(final KeyStore trustStore) throws Exception {
        final List<String> aliases = Collections.list(trustStore.aliases());
        Assert.assertEquals(1, aliases.size());
        Assert.assertEquals(ServerKey.KEY_ALIAS, aliases.get(0).toUpperCase());
    }

    private void isTrusted(final X509Certificate certificate, final KeyStore truststore) throws Exception {
        certificate.checkValidity();
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final CertPath cp = cf.generateCertPath(Collections.singletonList(certificate));
        final PKIXParameters params = new PKIXParameters(truststore);
        params.setRevocationEnabled(false);
        final CertPathValidator cpv = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
        final PKIXCertPathValidatorResult pkixCertPathValidatorResult =
                (PKIXCertPathValidatorResult) cpv.validate(cp, params);
        Assert.assertNotNull(pkixCertPathValidatorResult);

    }

    private KeyStore getTruststore(final String cartificate) throws InvalidKeystoreException {
        return new ServerKey(getClass().getResourceAsStream(cartificate)).getTrustStore();
    }

    private X509Certificate getEETCertificate(final String path) throws CertificateException {
        final InputStream is = getClass().getResourceAsStream(path);
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(is);
    }
}
