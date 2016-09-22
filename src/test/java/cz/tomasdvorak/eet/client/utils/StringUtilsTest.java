package cz.tomasdvorak.eet.client.utils;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {
    @Test
    public void leftPad() throws Exception {
        Assert.assertEquals("xxxxfoobar", StringUtils.leftPad("foobar", 10, 'x'));
        Assert.assertEquals("001234", StringUtils.leftPad("1234", 6, '0'));
    }

    @Test
    public void testSplitBlocks() throws Exception {
        final String[] blocks = StringUtils.splitBlocks("foobar", 3);
        Assert.assertEquals(2, blocks.length);
        Assert.assertEquals("foo", blocks[0]);
        Assert.assertEquals("bar", blocks[1]);
    }

    @Test
    public void testToBase64() throws Exception {
        final String actual = StringUtils.toBase64("lorem ipsum".getBytes("UTF-8"));
        Assert.assertEquals("bG9yZW0gaXBzdW0=", actual);

    }
}