package cz.tomasdvorak.eet.client.binding;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Ensure that every BigDecimal gets always 2 decimal digits.
 * Example: 324 -> 324.00
 */
public class BigDecimalAdapter extends XmlAdapter<String, BigDecimal> {

    /**
     * Parse BigDecimal based on provided String value. Null-safe: if the argument is null, null is returned
     */
    @Override
    public BigDecimal unmarshal(final String v) throws Exception {
        if(v != null && v.trim().length() > 0) {
            return new BigDecimal(v);
        } {
            return null;
        }
    }

    /**
     * Serialize BigDecimal to String representation. Null-safe, always two decimal digits are included.
     */
    @Override
    public String marshal(final BigDecimal v) throws Exception {
        if(v != null) {
            final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            return new DecimalFormat("0.00", symbols).format(v);
        } else {
            return null;
        }
    }
}
