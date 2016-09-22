package cz.tomasdvorak.eet.client.binding;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

public class BigDecimalAdapterTest {

    private BigDecimalAdapter bigDecimalAdapter;

    @Before
    public void setUp() throws Exception {
        bigDecimalAdapter = new BigDecimalAdapter();
    }

    /**
     * Some locales use comma instead of dot as a decimal separator. Test, that in this case BigDecimals still convert to
     * correct format
     */
    @Test
    public void testLocaleIndependentConversion() throws Exception {
        final Locale current = Locale.getDefault();
        try {
            final Locale czechLocale = new Locale("cs", "CZ");
            Locale.setDefault(czechLocale);
            Assert.assertEquals("123.05", bigDecimalAdapter.marshal(new BigDecimal("123.05")));
        } finally {
            Locale.setDefault(current);
        }
    }

    @Test
    public void unmarshal() throws Exception {
        Assert.assertEquals(new BigDecimal("123.56"), bigDecimalAdapter.unmarshal("123.56"));
        Assert.assertEquals(new BigDecimal("123"), bigDecimalAdapter.unmarshal("123"));
        Assert.assertNull(bigDecimalAdapter.unmarshal(null));
        Assert.assertNull(bigDecimalAdapter.unmarshal(""));
    }

    @Test
    public void marshal() throws Exception {
        Assert.assertEquals("123.05", bigDecimalAdapter.marshal(new BigDecimal("123.05")));
        Assert.assertEquals("123.00", bigDecimalAdapter.marshal(new BigDecimal("123")));
        Assert.assertNull(bigDecimalAdapter.marshal(null));
    }

}