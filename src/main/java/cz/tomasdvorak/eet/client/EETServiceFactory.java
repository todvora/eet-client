package cz.tomasdvorak.eet.client;

import cz.tomasdvorak.eet.client.dto.WebserviceConfiguration;
import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import cz.tomasdvorak.eet.client.security.ClientKey;
import cz.tomasdvorak.eet.client.security.ServerKey;

import java.io.InputStream;

/**
 * Factory for EET connections bound to clientKey. Every client should get one instance which is cachable for reuse.
 */
public final class EETServiceFactory {

    private EETServiceFactory() {} // utility class, no instance allowed

    /**
     * Produces EET service instance bound to a keystore and password.
     * @param clientKeyStream data stream of the keystore. Expected is only pkcs12 type of keystore, containing only one alias
     * @param password password to the keystore
     * @param serverKeyStream data stream of the root CA of the EET certificate. Stream will be closed automatically.
     */
    public static EETClient getInstance(final InputStream clientKeyStream, final String password, final InputStream serverKeyStream) throws InvalidKeystoreException {
        return new EETClientImpl(new ClientKey(clientKeyStream, password), new ServerKey(serverKeyStream), WebserviceConfiguration.DEFAULT);
    }

    /**
     * Produces EET service instance bound to a keystore and password with additional WS configuration (like connection timeout)
     * @param clientKeyStream data stream of the keystore. Expected is only pkcs12 type of keystore, containing only one alias
     * @param password password to the keystore
     * @param serverKeyStream data stream of the root CA of the EET certificate. Stream will be closed automatically.
     * @param wsConfiguration additional WS configuration (like timeout)
     */
    public static EETClient getInstance(final InputStream clientKeyStream, final String password, final InputStream serverKeyStream, final WebserviceConfiguration wsConfiguration) throws InvalidKeystoreException {
        return new EETClientImpl(new ClientKey(clientKeyStream, password), new ServerKey(serverKeyStream), wsConfiguration);
    }
}
