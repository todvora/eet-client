package cz.tomasdvorak.eet.client.utils;

import cz.tomasdvorak.eet.client.security.SecureEETCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public final class IOUtils {

    private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

    private IOUtils() {
        // utils class, no instance allowed
    }

    public static void closeQuietly(final InputStream stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (final IOException ignored) {
           logger.warn("Failed to close stream quietly", ignored);
        }
    }
}
