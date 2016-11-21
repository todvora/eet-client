package cz.tomasdvorak.eet.client.security;


import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSSecurityException;
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
import java.util.regex.Pattern;

public class ServerKeyTest {

    private KeyStore productionKeystore;
    private KeyStore playgroundKeystore;

    @Before
    public void setUp() throws Exception {
        playgroundKeystore = getTruststore("/keys/qica.der");
        productionKeystore = getTruststore("/keys/rca15_rsa.der", "/keys/2qca16_rsa.der");
    }

    @Test
    public void testImport() throws Exception {
        hasAliasCount(productionKeystore, 2);
        hasAliasCount(playgroundKeystore, 1);
    }

    @Test
    public void testNullStream() throws Exception {
        try {
            new ServerKey(getClass().getResourceAsStream("non-existent-file-12345"));
            Assert.fail("Should throw an exception!");
        } catch (final InvalidKeystoreException e) {
            Assert.assertTrue(e.getMessage().contains("cannot be NULL"));
        }
    }

    private void hasAliasCount(final KeyStore trustStore, final int aliasesCount) throws Exception {
        final List<String> aliases = Collections.list(trustStore.aliases());
        Assert.assertEquals(aliasesCount, aliases.size());
    }

    private KeyStore getTruststore(final String... certificate) throws InvalidKeystoreException {
        final InputStream[] streams = new InputStream[certificate.length];
        for(int i = 0; i< certificate.length; i++) {
            streams[i] = (getClass().getResourceAsStream(certificate[i]));
        }
        return new ServerKey(streams).getTrustStore();
    }
}
