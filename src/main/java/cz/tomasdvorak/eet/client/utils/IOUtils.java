package cz.tomasdvorak.eet.client.utils;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static void closeQuietly(final InputStream stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (final IOException ignored) {
        }
    }
}
