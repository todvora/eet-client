package cz.tomasdvorak.eet.client.security.crl;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Read all the CRLs from a X509Certificate instance. Based on bouncycastle rather than deprecated sun.security classes.
 */
final class CRLUtils {

    static List<URI> getCRLs(final X509Certificate cert) {
        final List<DistributionPoint> distributionPoints = getDistributionPoints(cert);
        final List<URI> result = new ArrayList<URI>();
        for(final DistributionPoint point : distributionPoints) {
            result.addAll(parseURI(point));
        }
        return result;
    }

    private static List<URI> parseURI(final DistributionPoint dp) {
        final List<URI> result = new ArrayList<URI>();
        final DistributionPointName dpn = dp.getDistributionPoint();
        if(isValidDistributionPoint(dpn)) {
            final List<GeneralName> generalNames = getGeneralNames(dpn);
            for(final GeneralName name : generalNames) {
                if(name.getTagNo() == GeneralName.uniformResourceIdentifier) {
                    final ASN1Encodable asn1Encodable = name.getName();
                    final String uri = DERIA5String.getInstance(asn1Encodable).getString();
                    result.add(toURI(uri));
                }
            }
        }
        return result;
    }

    private static boolean isValidDistributionPoint(final DistributionPointName dpn) {
        return dpn != null && dpn.getType() == DistributionPointName.FULL_NAME;
    }

    private static List<DistributionPoint> getDistributionPoints(final X509Certificate cert) {
        final List<DistributionPoint> result = new ArrayList<DistributionPoint>();
        final byte[] extensionValue = cert.getExtensionValue(Extension.cRLDistributionPoints.getId());

        if(extensionValue != null) {
            final ASN1OctetString instance = ASN1OctetString.getInstance(extensionValue);
            final byte[] octets = instance.getOctets();
            final CRLDistPoint distPoint = CRLDistPoint.getInstance(octets);
            final DistributionPoint[] distributionPoints = distPoint.getDistributionPoints();
            Collections.addAll(result, distributionPoints);
        }
        return result;
    }

    private static List<GeneralName> getGeneralNames(final DistributionPointName dpn) {
        final ASN1Encodable name = dpn.getName();
        return Arrays.asList(GeneralNames.getInstance(name).getNames());
    }

    private static URI toURI(final String uri) {
        try {
            return new URI(uri);
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
