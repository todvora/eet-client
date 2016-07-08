package cz.tomasdvorak.eet.client;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import cz.tomasdvorak.eet.client.security.ClientKey;

import java.io.InputStream;

public class EETServiceFactory {
    /**
     * Creates EET service instance bound to a keystore and password.
     * @param keystoreStream data stream of the keystore. Expected is only pkcs12 type of keystore, containing only one alias
     * @param password password to the keystore
     */
    public static EETClient getInstance(final InputStream keystoreStream, final String password) throws InvalidKeystoreException {
        return new EETClientImpl(new ClientKey(keystoreStream, password));
    }
}
