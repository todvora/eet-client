package cz.tomasdvorak.eet.client.networking;

import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.dto.DnsResolver;
import cz.tomasdvorak.eet.client.exceptions.DnsLookupFailedException;
import cz.tomasdvorak.eet.client.exceptions.DnsTimeoutException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class DnsLookupWithTimeoutTest {

    private DnsLookup resolver;

    @Before
    public void setUp() throws Exception {

        final DnsResolver resolverImpl = new DnsResolver() {
            @Override
            public String getHostAddress(final String host) throws UnknownHostException {
                if ("pg.eet.cz".equals(host)) {
                    return "5.145.105.129";
                } else if ("nonsense-timeouting.tomas-dvorak.cz".equals(host)) {
                    try {
                        Thread.sleep(10000);
                        return "5.145.105.129";
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new UnknownHostException(host);
                }
            }
        };
        this.resolver = new DnsLookupWithTimeout(resolverImpl, 100);
    }

    @Test(timeout = 10000)
    public void resolveOk() throws Exception {
        final String resolved = resolver.resolveAddress(EndpointType.PLAYGROUND.getWebserviceUrl());
        Assert.assertEquals("5.145.105.129", resolved);
    }

    @Test(timeout = 10000)
    public void testResolveTimeout() {
        try {
            resolver.resolveAddress("http://nonsense-timeouting.tomas-dvorak.cz/ws/");
            Assert.fail("Should throw an exception");
        } catch (DnsLookupFailedException e) {
            Assert.fail("Should throw an timeout exception");
        } catch (DnsTimeoutException e) {
            final Throwable cause = e.getCause();
            Assert.assertEquals(TimeoutException.class, cause.getClass());
            Assert.assertEquals("DNS Lookup for host nonsense-timeouting.tomas-dvorak.cz timed out", e.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testResolveUnknownHost() {
        try {
            resolver.resolveAddress("http://nonsense-nonexistent.tomas-dvorak.cz/ws/");
            Assert.fail("Should throw an exception");
        } catch (DnsLookupFailedException e) {
            final Throwable cause = e.getCause();
            Assert.assertEquals(UnknownHostException.class, cause.getClass());
            Assert.assertEquals("nonsense-nonexistent.tomas-dvorak.cz", cause.getMessage());
        } catch (DnsTimeoutException e) {
            Assert.fail("Should throw an timeout exception");

        }
    }

    @Test(timeout = 10000)
    public void testResolveMalformedUrl() {
        try {
            resolver.resolveAddress("/foo/bar");
            Assert.fail("Should throw an exception");
        } catch (DnsLookupFailedException e) {
            final Throwable cause = e.getCause();
            Assert.assertEquals(MalformedURLException.class, cause.getClass());
            Assert.assertEquals("no protocol: /foo/bar", cause.getMessage());
        } catch (DnsTimeoutException e) {
            Assert.fail("Should throw an timeout exception");

        }
    }
}