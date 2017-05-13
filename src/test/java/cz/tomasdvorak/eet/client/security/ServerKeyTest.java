package cz.tomasdvorak.eet.client.security;


import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
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
        playgroundKeystore = getTruststore("/certificates/qica.der");
        productionKeystore = getTruststore("/certificates/rca15_rsa.der", "/certificates/2qca16_rsa.der");
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

    @Test
    public void testEmbeddedCertificates() throws Exception {
        final ServerKey serverKey = ServerKey.trustingEmbeddedCertificates();
        final ArrayList<String> aliases = Collections.list(serverKey.getTrustStore().aliases());
        Assert.assertEquals(3, aliases.size());
    }

    @Test
    public void testFromClasspathResource() throws Exception {
        final ServerKey serverKey = ServerKey.fromInputStream(getClass().getResourceAsStream("/certificates/qica.der"));
        hasAliasCount(serverKey.getTrustStore(), 1);
    }

    @Test
    public void testFromFile() throws Exception {
        final String filePath = getClass().getResource("/certificates/qica.der").getFile();
        final ServerKey serverKey = ServerKey.fromFile(filePath);
        hasAliasCount(serverKey.getTrustStore(), 1);
    }

    @Test
    public void testFromNonexistentFile() throws Exception {
        try {
            ServerKey.fromFile("non-existent-file-12345");
        } catch (final InvalidKeystoreException e) {
            Assert.assertTrue(e.getCause() instanceof FileNotFoundException);
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
