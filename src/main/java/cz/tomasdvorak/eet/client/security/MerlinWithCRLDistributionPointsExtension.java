package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.RevocationListException;
import cz.tomasdvorak.eet.client.security.crl.InMemoryCRLStore;
import org.apache.logging.log4j.Logger;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSSecurityException;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * On the fly parsing, download and set of CRLs list, based on CRL Distribution Points extension of the certificate.
 *
 * see also: https://security.stackexchange.com/questions/72570/is-publishing-crls-over-http-a-potential-vulnerability
 */
class MerlinWithCRLDistributionPointsExtension extends Merlin {

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(MerlinWithCRLDistributionPointsExtension.class);


    @Override
    public void verifyTrust(final X509Certificate[] certs, final boolean enableRevocation, final Collection<Pattern> subjectCertConstraints) throws WSSecurityException {
        if (enableRevocation) {
            try {
                this.setCRLCertStore(InMemoryCRLStore.INSTANCE.getCRLStore(certs));
            } catch (final RevocationListException e) {
                throw new WSSecurityException(WSSecurityException.ErrorCode.SECURITY_ERROR, e);
            }
        } else {
            logger.warn("Certificate revocation lists checking is disabled.");
        }
        super.verifyTrust(certs, enableRevocation, subjectCertConstraints);
    }
}
