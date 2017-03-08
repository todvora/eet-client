package cz.tomasdvorak.eet.client.networking;

import cz.tomasdvorak.eet.client.dto.DnsResolver;
import cz.tomasdvorak.eet.client.exceptions.DnsLookupFailedException;
import cz.tomasdvorak.eet.client.exceptions.DnsTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;

public class DnsLookupWithTimeout implements DnsLookup {

    private static final Logger logger = LogManager.getLogger(DnsLookupWithTimeout.class);

    private final long timeoutMillis;
    private final DnsResolver dnsResolver;

    public DnsLookupWithTimeout(final DnsResolver dnsResolver, final long timeoutMillis) {
        this.dnsResolver = dnsResolver;
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public String resolveAddress(final String url) throws DnsLookupFailedException, DnsTimeoutException {
        final String hostname;
        try {
            hostname = new URL(url).getHost();
        } catch (MalformedURLException e) {
            throw new DnsLookupFailedException(String.format("URL %s is malformed", url), e);
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<String> result = executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return dnsResolver.getHostAddress(hostname);
                }
            });
            return result.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("Unexpected interrupton while resolving hostname " + hostname, e);
            Thread.currentThread().interrupt();
            throw new DnsLookupFailedException("Unexpected interruption while resolving hostname " + hostname, e);
        } catch (ExecutionException e) {
            throw new DnsLookupFailedException("Failed resolving hostname " + hostname, e.getCause());
        } catch (TimeoutException e) {
            throw new DnsTimeoutException(String.format("DNS Lookup for host %s timed out", hostname), e);
        } finally {
            executor.shutdownNow();
        }
    }
}