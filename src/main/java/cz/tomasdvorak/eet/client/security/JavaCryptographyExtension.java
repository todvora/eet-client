package cz.tomasdvorak.eet.client.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;

/**
 * Validate that current JRE has unlimited keys length enabled.
 * More info: https://github.com/todvora/eet-client#java-cryptography-extension-jce-unlimited-strength
 */
class JavaCryptographyExtension {

    private static final Logger logger = LogManager.getLogger(JavaCryptographyExtension.class);

    static void validateInstallation() {
        try {
            final int maxKeyLen = Cipher.getMaxAllowedKeyLength("SHA256withRSA");
            if (maxKeyLen <= 128) {
                logger.warn("Limited cryptography stenghth detected. Only allowed keys of length " + maxKeyLen + ". " +
                        "Production communication requires unlimited cryptography strength. " +
                        "More info: https://github.com/todvora/eet-client#java-cryptography-extension-jce-unlimited-strength");
            }
        } catch (NoSuchAlgorithmException e) {
            logger.warn("Failed to verify JCE installation", e);
        }
    }
}
