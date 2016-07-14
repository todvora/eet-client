package cz.tomasdvorak.eet.client.security.crl;

import cz.tomasdvorak.eet.client.exceptions.RevocationListException;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.URIName;
import sun.security.x509.X509CertImpl;

import java.io.IOException;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

class CRLUtils {
    static List<URI> getCRLs(final X509Certificate cert) throws RevocationListException {
        final List<URI> result = new ArrayList<>();
        if (cert instanceof X509CertImpl) {
            final X509CertImpl x509 = (X509CertImpl) cert;
            final CRLDistributionPointsExtension crlDistributionPointsExtension = x509.getCRLDistributionPointsExtension();
            try {
                final List<DistributionPoint> distributionPoints = crlDistributionPointsExtension.get(CRLDistributionPointsExtension.POINTS);
                distributionPoints.forEach(point -> {
                    final sun.security.x509.GeneralNames name = point.getFullName();
                    for (int i = 0; i < name.size(); i++) {
                        final URI uri = ((URIName) name.get(i).getName()).getURI();
                        result.add(uri);
                    }
                });
            } catch (final IOException e) {
                throw new RevocationListException(e);
            }
        }
        return result;

    }
}
