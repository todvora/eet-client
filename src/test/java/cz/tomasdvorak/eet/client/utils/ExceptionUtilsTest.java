package cz.tomasdvorak.eet.client.utils;


import org.junit.Assert;
import org.junit.Test;

import java.net.SocketTimeoutException;

public class ExceptionUtilsTest {

    @Test
    public void testContainsDirectType() throws Exception {
        Assert.assertTrue(ExceptionUtils.containsExceptionType(new SocketTimeoutException("timeout!"), SocketTimeoutException.class));
    }

    @Test
    public void testContainsNestedType() throws Exception {
        Assert.assertTrue(ExceptionUtils.containsExceptionType(new Throwable(new RuntimeException(new SocketTimeoutException("timeout!"))), SocketTimeoutException.class));
    }

    @Test
    public void testContainsNot() throws Exception {
        Assert.assertFalse(ExceptionUtils.containsExceptionType(new Throwable(new RuntimeException(new SocketTimeoutException("timeout!"))), IllegalArgumentException.class));
    }

    @Test
    public void testContainsNull() throws Exception {
        Assert.assertFalse(ExceptionUtils.containsExceptionType(null, IllegalArgumentException.class));

    }
}