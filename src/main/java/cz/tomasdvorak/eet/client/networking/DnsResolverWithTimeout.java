package cz.tomasdvorak.eet.client.networking;

import cz.tomasdvorak.eet.client.exceptions.DnsLookupFailedException;
import cz.tomasdvorak.eet.client.exceptions.DnsTimeoutException;
import cz.tomasdvorak.eet.client.security.SecureEETCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class DnsResolverWithTimeout implements DnsResolver {

    private static final Logger logger = LoggerFactory.getLogger(DnsResolverWithTimeout.class);

    private final long timeoutMillis;

    public DnsResolverWithTimeout(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public String resolveAddress(final String url) throws DnsLookupFailedException, DnsTimeoutException {
        final String host;
        try {
            host = new URL(url).getHost();
        } catch (MalformedURLException e) {
            throw new DnsLookupFailedException(String.format("URL %s is malformed", url), e);
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<String> result = executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return doResolve(host);
                }
            });
            return result.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("Unexpected interrupton while resolving host " + host, e);
            Thread.currentThread().interrupt();
            throw new DnsLookupFailedException("Unexpected interruption while resolving host " + host, e);
        } catch (ExecutionException e) {
            throw new DnsLookupFailedException("Failed resolving host " + host, e.getCause());
        } catch (TimeoutException e) {
            throw new DnsTimeoutException(String.format("DNS Lookup for host %s timed out", host), e);
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Internal method to allow easier unit testing without dependency on internet connection
     */
    protected String doResolve(final String host) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(host);
        return address.getHostAddress();
    }
}
