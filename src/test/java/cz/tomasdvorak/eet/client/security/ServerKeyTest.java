package cz.tomasdvorak.eet.client.security;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class ServerKeyTest {

    private ServerKey serverKey;

    @Before
    public void setUp() throws Exception {
        serverKey = new ServerKey(
                getClass().getResourceAsStream("/keys/qica.der"),
                getClass().getResourceAsStream("/keys/revocated.crl")
        );
    }

    @Test
    public void testCertificateImport() throws Exception {
        List<String> aliases = Collections.list(serverKey.getTruststore().aliases());
        Assert.assertEquals(1, aliases.size());
        Assert.assertEquals("ICA", aliases.get(0).toUpperCase());
    }
}