package cz.tomasdvorak.eet.client.utils;

import cz.tomasdvorak.eet.client.binding.XmlDateAdapter;

import java.util.Date;

public class DateUtils {

    private static final XmlDateAdapter DATE_ADAPTER = new XmlDateAdapter();

    public static Date parse(final String date) {
        try {
            return DATE_ADAPTER.unmarshal(date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String format(final Date date) {
        try {
            return DATE_ADAPTER.marshal(date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
