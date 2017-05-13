package cz.tomasdvorak.eet.client.dto;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Implement this interface if you want to use dns lookup different than default implementation based on {@link InetAddress}.
 * You can for example use http://www.xbill.org/dnsjava/ implementation or any other custom logic.
 */
public interface DnsResolver {

    /**
     * Convert a domain name to IP address.
     *
     * @param hostname Pure hostname, without protocol or path. For example pg.eet.cz
     * @return IP address as a String
     * @throws UnknownHostException in case DNS lookup fails.
     */
    String getHostAddress(String hostname) throws UnknownHostException;
}
