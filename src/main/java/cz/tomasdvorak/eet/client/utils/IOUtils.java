package cz.tomasdvorak.eet.client.utils;

import cz.tomasdvorak.eet.client.security.SecureEETCommunication;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public final class IOUtils {

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(SecureEETCommunication.class);

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
