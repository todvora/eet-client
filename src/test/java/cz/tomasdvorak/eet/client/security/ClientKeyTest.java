package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.junit.Assert;
import org.junit.Test;

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
}