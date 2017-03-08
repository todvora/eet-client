package cz.tomasdvorak.eet.client.networking;

import cz.tomasdvorak.eet.client.dto.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressDnsResolver implements DnsResolver {

    @Override
    public String getHostAddress(final String hostname) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(hostname);
        return address.getHostAddress();
    }
}
