package cz.tomasdvorak.eet.client.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class IOUtilsTest {

    @Test
    public void closeQuietly() {
        final InputStream is = this.getClass().getResourceAsStream("/keys/README.md");
        IOUtils.closeQuietly(is);
        try {
            is.read();
            Assert.fail("Should throw an exception");
        } catch (final IOException e) {
            Assert.assertEquals("Stream closed", e.getMessage());
        }
    }

    @Test
    public void closeQuietlyWithNull() {
        IOUtils.closeQuietly(null); // should not throw any exceptions!
    }

}