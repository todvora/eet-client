package cz.tomasdvorak.eet.client.utils;

import javax.xml.bind.DatatypeConverter;

public final class StringUtils {

    private StringUtils() {
        // utils class, no instance allowed
    }

    public static String leftPad(final String value, final int length, final char paddingChar) {
        final StringBuilder sb = new StringBuilder(value);
        while (sb.length() < length) {
            sb.insert(0, paddingChar);
        }
        return sb.toString();
    }

    public static String[] splitBlocks(final String inputText, final int blockSize) {
        return inputText.split("(?<=\\G.{" + blockSize + "})");
    }

    public static String toBase64(final byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }

    public static boolean isEmpty(final String text) {
        return text == null || text.isEmpty() || isOnlyWhitespace(text);
    }

    private static boolean isOnlyWhitespace(final String text) {
        return text.matches("\\s+");
    }
}
