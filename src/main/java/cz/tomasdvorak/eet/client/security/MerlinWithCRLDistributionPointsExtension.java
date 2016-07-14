package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.RevocationListException;
import cz.tomasdvorak.eet.client.security.crl.InMemoryCRLStore;
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
    @Override
    public void verifyTrust(final X509Certificate[] certs, final boolean enableRevocation, final Collection<Pattern> subjectCertConstraints) throws WSSecurityException {
        if (enableRevocation) {
            try {
                this.setCRLCertStore(InMemoryCRLStore.INSTANCE.getCRLStore(certs));
            } catch (final RevocationListException e) {
                throw new WSSecurityException(WSSecurityException.ErrorCode.SECURITY_ERROR, e);
            }
        }
        super.verifyTrust(certs, enableRevocation, subjectCertConstraints);
    }
}
