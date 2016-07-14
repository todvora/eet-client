package cz.tomasdvorak.eet.client.security;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class ServerKeyTest {

    private ServerKey serverKey;

    @Before
    public void setUp() throws Exception {
        serverKey = new ServerKey(getClass().getResourceAsStream("/keys/qica.der"));
    }

    @Test
    public void testCertificateImport() throws Exception {
        final List<String> aliases = Collections.list(serverKey.getTrustStore().aliases());
        Assert.assertEquals(1, aliases.size());
        Assert.assertEquals(ServerKey.KEY_ALIAS, aliases.get(0).toUpperCase());
    }
}
