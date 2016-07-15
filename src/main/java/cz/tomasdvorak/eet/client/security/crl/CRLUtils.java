package cz.tomasdvorak.eet.client.security.crl;

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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Read all the CRLs from a X509Certificate instance. Based on bouncycastle rather than deprecated sun.security classes.
 */
class CRLUtils {

    static List<URI> getCRLs(final X509Certificate cert) {
        return getDistributionPoints(cert)
                .flatMap(CRLUtils::parseURI)
                .collect(Collectors.toList());
    }

    private static Stream<URI> parseURI(final DistributionPoint dp) {
        return Stream.of(dp.getDistributionPoint())
                .filter(CRLUtils::isValidDistributionPoint)
                .flatMap(CRLUtils::getGeneralNames)
                .filter(genName -> genName.getTagNo() == GeneralName.uniformResourceIdentifier)
                .map(GeneralName::getName)
                .map(DERIA5String::getInstance)
                .map(DERIA5String::getString)
                .map(CRLUtils::toURI);
    }

    private static boolean isValidDistributionPoint(final DistributionPointName dpn) {
        return dpn != null && dpn.getType() == DistributionPointName.FULL_NAME;
    }

    private static Stream<DistributionPoint> getDistributionPoints(final X509Certificate cert) {
        return Optional.ofNullable(cert.getExtensionValue(Extension.cRLDistributionPoints.getId()))
                .map(ASN1OctetString::getInstance)
                .map(ASN1OctetString::getOctets)
                .map(CRLDistPoint::getInstance)
                .map(CRLDistPoint::getDistributionPoints)
                .map(Arrays::stream)
                .orElse(Stream.empty());
    }

    private static Stream<GeneralName> getGeneralNames(final DistributionPointName dpn) {
        return Stream.of(dpn)
                .map(DistributionPointName::getName)
                .map(GeneralNames::getInstance)
                .map(GeneralNames::getNames)
                .flatMap(Arrays::stream);
    }

    private static URI toURI(final String uri) {
        try {
            return new URI(uri);
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
