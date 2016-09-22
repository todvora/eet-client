package cz.tomasdvorak.eet.client.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class StringJoinerTest {
    @Test
    public void testAdd() throws Exception {
        final StringJoiner joiner = new StringJoiner("-");
        joiner.add("A");
        joiner.add("B");
        joiner.add("C");
        Assert.assertEquals("A-B-C", joiner.toString());
    }

    @Test
    public void testJoin() throws Exception {
        final String actual = StringJoiner.join(":", Arrays.asList("foo", "bar", "baz"));
        Assert.assertEquals("foo:bar:baz", actual);

    }
}