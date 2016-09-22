package cz.tomasdvorak.eet.client.utils;

import cz.tomasdvorak.eet.client.binding.BigDecimalAdapter;
import cz.tomasdvorak.eet.client.binding.XmlDateAdapter;

import java.math.BigDecimal;
import java.util.Date;

public final class NumberUtils {

    private static final BigDecimalAdapter BIGDECIMAL_ADAPTER = new BigDecimalAdapter();

    public static BigDecimal parse(final String date) {
        try {
            return BIGDECIMAL_ADAPTER.unmarshal(date);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String format(final BigDecimal date) {
        try {
            return BIGDECIMAL_ADAPTER.marshal(date);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
