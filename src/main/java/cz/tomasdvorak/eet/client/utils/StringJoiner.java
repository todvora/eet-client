package cz.tomasdvorak.eet.client.utils;

import java.util.LinkedList;
import java.util.List;

public class StringJoiner {


    private final String separator;
    private final List<CharSequence> items;

    public StringJoiner(final String separator) {
        this.separator = separator;
        items = new LinkedList<CharSequence>();
    }

    public void add(final CharSequence item) {
        items.add(item);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final CharSequence item : items) {
            stringBuilder.append(item).append(separator);
        }
        final String result = stringBuilder.toString();
        return result.substring(0, result.length() - separator.length()); // remove the last one separator
    }

    public static String join(final String delimiter, final Iterable<? extends CharSequence> elements) {
        final StringJoiner stringJoiner = new StringJoiner(delimiter);
        for (final CharSequence item : elements) {
            stringJoiner.add(item);
        }
        return stringJoiner.toString();

    }
}
