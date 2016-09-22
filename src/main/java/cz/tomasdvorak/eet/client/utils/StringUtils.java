package cz.tomasdvorak.eet.client.utils;

import javax.xml.bind.DatatypeConverter;

public class StringUtils {
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
}
