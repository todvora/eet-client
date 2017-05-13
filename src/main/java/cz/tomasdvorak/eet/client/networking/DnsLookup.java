package cz.tomasdvorak.eet.client.networking;

import cz.tomasdvorak.eet.client.exceptions.DnsLookupFailedException;
import cz.tomasdvorak.eet.client.exceptions.DnsTimeoutException;

public interface DnsLookup {
    String resolveAddress(String url) throws DnsLookupFailedException, DnsTimeoutException;
}
