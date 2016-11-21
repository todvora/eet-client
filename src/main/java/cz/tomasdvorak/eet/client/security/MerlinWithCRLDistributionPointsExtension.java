package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.exceptions.RevocationListException;
import cz.tomasdvorak.eet.client.security.crl.InMemoryCRLStore;
import org.apache.logging.log4j.Logger;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSSecurityException;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
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
            final List<X509Certificate> x509Certificates = new ArrayList<X509Certificate>(Arrays.asList(certs));
            try {
                try {
                    final KeyStore keyStore = this.getTrustStore();
                    final Enumeration<String> aliases = keyStore.aliases();
                    while(aliases.hasMoreElements()) {
                        final String alias = aliases.nextElement();
                        final Certificate cert = keyStore.getCertificate(alias);
                        if(cert instanceof X509Certificate) {
                            x509Certificates.add((X509Certificate) cert);
                        }
                    }
                } catch (final KeyStoreException e) {
                    e.printStackTrace();
                }

                this.setCRLCertStore(InMemoryCRLStore.INSTANCE.getCRLStore(x509Certificates.toArray(new X509Certificate[x509Certificates.size()])));
            } catch (final RevocationListException e) {
                throw new WSSecurityException(WSSecurityException.ErrorCode.SECURITY_ERROR, e);
            }
        } else {
            logger.warn("Certificate revocation lists checking is disabled.");
        }
        super.verifyTrust(certs, enableRevocation, subjectCertConstraints);
    }
}
