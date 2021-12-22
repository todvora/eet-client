package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

public class ClientKeyTest {

    @Test
    public void testNullStream() throws Exception {
        try {
            new ClientKey(getClass().getResourceAsStream("non-existent-file-12345"), "eet");
            Assert.fail("Should throw an exception!");
        } catch (final InvalidKeystoreException e) {
            Assert.assertTrue(e.getMessage().contains("cannot be NULL"));
        }
    }

    @Test
    public void testClientKeyByInputStream() throws Exception {
        final ClientKey clientKey = ClientKey.fromInputStream(getClass().getResourceAsStream("/keys/CZ1212121218.p12"), "eet");
        Assert.assertEquals("1", clientKey.getAlias());
    }

    @Test
    public void testClientKeyByFile() throws Exception {
        final String resourceUri = getClass().getResource("/keys/CZ1212121218.p12").getFile();
        final ClientKey clientKey = ClientKey.fromFile(resourceUri, "eet");
        Assert.assertEquals("1", clientKey.getAlias());
    }

    @Test
    public void testClientKeyByNonexistentFile() throws Exception {
        try {
            ClientKey.fromFile("non-existent-file-12345", "eet");
        } catch (InvalidKeystoreException e) {
            Assert.assertTrue(e.getCause() instanceof FileNotFoundException);
        }
    }

}