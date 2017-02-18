package cz.tomasdvorak.eet.client.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wss4j.common.crypto.Merlin;

import java.security.Security;

class MerlinWithCRLDistributionPointsExtension extends Merlin {

    private static final Logger logger = LogManager.getLogger(MerlinWithCRLDistributionPointsExtension.class);

    public MerlinWithCRLDistributionPointsExtension() {
        configureSystemProperties();
    }

    private void configureSystemProperties() {
        final boolean crlDownloadEnabled = Boolean.getBoolean("com.sun.security.enableCRLDP");
        final boolean checkRevocationEnabled = Boolean.getBoolean("com.sun.net.ssl.checkRevocation");
        final String value = Security.getProperty("com.sun.security.onlyCheckRevocationOfEECert");
        final boolean onlyCheckRevocationOfEECert = (value != null) && value.equalsIgnoreCase("true");

        if (!crlDownloadEnabled || !checkRevocationEnabled || !onlyCheckRevocationOfEECert) {
            logger.info("System properties will be configured to enable certificate revocation checks.");
            System.setProperty("com.sun.security.enableCRLDP", "true");
            System.setProperty("com.sun.net.ssl.checkRevocation", "true");
            Security.setProperty("com.sun.security.onlyCheckRevocationOfEECert", "true"); // verify only revocation of the last cert in path (the EET cert)
        }
    }
}
