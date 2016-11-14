package cz.tomasdvorak.eet.client.utils;

import cz.tomasdvorak.eet.client.binding.BigDecimalAdapter;

import java.math.BigDecimal;

/**
 * @see BigDecimalAdapter
 *
 */
public final class NumberUtils {

    private static final BigDecimalAdapter BIGDECIMAL_ADAPTER = new BigDecimalAdapter();

    private NumberUtils() {
        // utils class, no instance allowed
    }

    public static BigDecimal parse(final String date) {
        return BIGDECIMAL_ADAPTER.unmarshal(date);
    }

    public static String format(final BigDecimal date) {
        return BIGDECIMAL_ADAPTER.marshal(date);
    }
}
