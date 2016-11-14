package cz.tomasdvorak.eet.client.utils;

import java.security.cert.X509Certificate;

public final class CertificateUtils {

    private CertificateUtils() {
        // utility class, no instance
    }

    public static String getCertificateInfo(final X509Certificate cert) {
        return "{" +
                "subject='" + cert.getSubjectDN() + '\'' +
                ", issuer='" + cert.getIssuerDN() + '\'' +
                ", SerialNumber=" + cert.getSerialNumber() +
                ", validFrom=" + DateUtils.format(cert.getNotBefore()) +
                ", validTo=" + DateUtils.format(cert.getNotAfter()) +
                '}';
    }
}
