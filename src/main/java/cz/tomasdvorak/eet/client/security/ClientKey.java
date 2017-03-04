package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.DataSigningException;
import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import cz.tomasdvorak.eet.client.utils.CertificateUtils;
import cz.tomasdvorak.eet.client.utils.IOUtils;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;

/**
 * Representation of the client private key and public certificate pair. The public certificate is attached to every
 * signed request, signature is computed from the private key.
 */
public class ClientKey {

    private static final Logger logger = LoggerFactory.getLogger(ClientKey.class);

    private final KeyStore keyStore;
    private final String password;
    private final String alias;
    private final ClientPasswordCallback clientPasswordCallback;

    /**
     * Create new ClientKey instance based on data provided in the stream together with the password
     * @deprecated use
     * @param inputStream expects a stream to the pk12 keystore with one pair of key/cert. Will be closed automatically
     */
    public ClientKey(final InputStream inputStream, final String password) throws InvalidKeystoreException {

        if(inputStream == null) {
            throw new InvalidKeystoreException("Input stream of ClientKey cannot be NULL");
        }

        JavaCryptographyExtension.validateInstallation();

        this.password = password;
        String tempAlias = null;
        final KeyStore keystore = getKeyStore(inputStream, password);
        final Enumeration<String> aliases = getAliases(keystore);
        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            try {
				if (keystore.isKeyEntry(alias)) {
					tempAlias = alias;
					logger.info(CertificateUtils.getCertificateInfo(keystore, alias));
				}
			} catch (final KeyStoreException e) {
				logger.error(String.format("cannot check isKeyEntry(%s) - %s : %s", alias, e.getClass().getName(), e.getMessage()));
			}
        }
        if (tempAlias == null) {
            throw new InvalidKeystoreException("Keystore doesn't contain any keys!");
        }
        this.alias = tempAlias;
        this.keyStore = keystore;
        this.clientPasswordCallback = new ClientPasswordCallback(alias, password);
    }

    /**
     * @since 3.0
     */
    public static ClientKey fromInputStream(final InputStream inputStream, final String password) throws InvalidKeystoreException {
        return new ClientKey(inputStream, password);
    }

    /**
     * @since 3.0
     */
    public static ClientKey fromFile(final String filePath, final String password) throws InvalidKeystoreException {
        try {
            return new ClientKey(new FileInputStream(filePath), password);
        } catch (FileNotFoundException e) {
            throw new InvalidKeystoreException(e);
        }
    }

    private Enumeration<String> getAliases(final KeyStore keystore) throws InvalidKeystoreException {
        try {
            return keystore.aliases();
        } catch (final KeyStoreException e) {
            throw new InvalidKeystoreException(e);
        }
    }

    private KeyStore getKeyStore(final InputStream inputStream, final String password) throws InvalidKeystoreException {
        try {
            final KeyStore keystore = KeyStore.getInstance("pkcs12", new BouncyCastleProvider());
            keystore.load(inputStream, password.toCharArray());
            inputStream.close();
            return keystore;
        } catch (final CertificateException e) {
            throw new InvalidKeystoreException(e);
        } catch (final NoSuchAlgorithmException e) {
            throw new InvalidKeystoreException(e);
        } catch (final KeyStoreException e) {
            throw new InvalidKeystoreException(e);
        } catch (final IOException e) {
            throw new InvalidKeystoreException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Sign provided text with SHA256withRSA initialized by the private key
     */
    public byte[] sign(final String text) throws DataSigningException {
        try {
            final Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(getPrivateKey());
            signature.update(text.getBytes("UTF-8"));
            return signature.sign();
        } catch (final NoSuchAlgorithmException e) {
            throw new DataSigningException(e);
        } catch (final UnrecoverableKeyException e) {
            throw new DataSigningException(e);
        } catch (final InvalidKeyException e) {
            throw new DataSigningException(e);
        } catch (final SignatureException e) {
            throw new DataSigningException(e);
        } catch (final UnsupportedEncodingException e) {
            throw new DataSigningException(e);
        } catch (final KeyStoreException e) {
            throw new DataSigningException(e);
        }
    }

    private PrivateKey getPrivateKey() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        return (PrivateKey) this.keyStore.getKey(this.alias, this.password.toCharArray());
    }

    /**
     * Crypto implementation used to sign WS requests
     */
    public Crypto getCrypto() {
        final Merlin merlin = new Merlin();
        merlin.setKeyStore(this.keyStore);
        return merlin;
    }

    /**
     * Get the first (and hopefully the only one) alias included in the keystore bundle
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Callback supplying username / password  combination to the WS signing layer
     */
    public ClientPasswordCallback getClientPasswordCallback() {
        return clientPasswordCallback;
    }
}
