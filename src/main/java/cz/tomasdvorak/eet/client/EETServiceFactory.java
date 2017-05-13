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
     * Deprecated - use {@link #getInstance(ClientKey, ServerKey)} method instead
     * @param clientKeyStream data stream of the keystore. Expected is only pkcs12 type of keystore, containing only one alias
     * @param password password to the keystore
     * @param serverKeyStream data stream of the root CA of the EET certificate. Stream will be closed automatically.
     */
    @Deprecated
    public static EETClient getInstance(final InputStream clientKeyStream, final String password, final InputStream... serverKeyStream) throws InvalidKeystoreException {
        return new EETClientImpl(ClientKey.fromInputStream(clientKeyStream, password), ServerKey.fromInputStream(serverKeyStream), WebserviceConfiguration.DEFAULT);
    }

    /**
     * Produces EET service instance bound to a keystore and password with additional WS configuration (like connection timeout)
     * Deprecated - use {@link #getInstance(ClientKey, ServerKey, WebserviceConfiguration)} method instead
     * @param wsConfiguration additional WS configuration (like timeout)
     * @param clientKeyStream data stream of the keystore. Expected is only pkcs12 type of keystore, containing only one alias
     * @param password password to the keystore
     * @param serverKeyStream data stream of the root CA of the EET certificate. Stream will be closed automatically.
     */
    @Deprecated
    public static EETClient getInstance(final WebserviceConfiguration wsConfiguration, final InputStream clientKeyStream, final String password, final InputStream... serverKeyStream) throws InvalidKeystoreException {
        return new EETClientImpl(ClientKey.fromInputStream(clientKeyStream, password), ServerKey.fromInputStream(serverKeyStream), wsConfiguration);
    }

    /**
     * @since 3.0
     */
    public static EETClient getInstance(final ClientKey clientKey, final ServerKey serverKey) {
        return new EETClientImpl(clientKey, serverKey, WebserviceConfiguration.DEFAULT);
    }

    /**
     * @since 3.0
     */
    public static EETClient getInstance(final ClientKey clientKey, final ServerKey serverKey, final WebserviceConfiguration wsConfiguration) {
        return new EETClientImpl(clientKey, serverKey, wsConfiguration);
    }
}
