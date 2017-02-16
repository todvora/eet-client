package cz.tomasdvorak.eet.client.utils;

import cz.tomasdvorak.eet.client.binding.XmlDateAdapter;
import cz.tomasdvorak.eet.client.exceptions.InvalidFormatException;

import java.text.ParseException;
import java.time.ZonedDateTime;

public final class DateUtils {

    private static final XmlDateAdapter DATE_ADAPTER = new XmlDateAdapter();

    private DateUtils() {
        // utils class, no instance allowed
    }

    public static ZonedDateTime parse(final String date) {
        try {
            return DATE_ADAPTER.unmarshal(date);
        } catch (final ParseException e) {
            throw new InvalidFormatException(e);
        }
    }

    public static String format(final ZonedDateTime date) {
        return DATE_ADAPTER.marshal(date);
    }
}
