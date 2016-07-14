package cz.tomasdvorak.eet.client;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import cz.tomasdvorak.eet.client.security.ClientKey;
import cz.tomasdvorak.eet.client.security.ServerKey;

import java.io.InputStream;

public final class EETServiceFactory {
    /**
     * Creates EET service instance bound to a keystore and password.
     * @param clientKeyStream data stream of the keystore. Expected is only pkcs12 type of keystore, containing only one alias
     * @param password password to the keystore
     */
    public static EETClient getInstance(final InputStream clientKeyStream, final String password, final InputStream serverKeyStream) throws InvalidKeystoreException {
        return new EETClientImpl(new ClientKey(clientKeyStream, password), new ServerKey(serverKeyStream));
    }
}
