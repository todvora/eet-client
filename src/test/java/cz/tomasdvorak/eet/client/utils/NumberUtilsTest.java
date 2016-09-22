package cz.tomasdvorak.eet.client.utils;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class NumberUtilsTest {
    @Test
    public void parse() throws Exception {
        final BigDecimal actual = NumberUtils.parse("123.45");
        Assert.assertEquals(new BigDecimal("123.45"), actual);
    }

    @Test
    public void format() throws Exception {
        final String actual = NumberUtils.format(new BigDecimal("123"));
        Assert.assertEquals("123.00", actual);
    }

}