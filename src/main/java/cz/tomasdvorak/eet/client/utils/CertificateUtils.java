package cz.tomasdvorak.eet.client.utils;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;

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

    public static String getCertificateInfo(final KeyStore keystore, final String alias) throws InvalidKeystoreException {
        try {
            final X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
            final List<String> types = new LinkedList<String>();
            if (keystore.isKeyEntry(alias)) {
                types.add("keyEntry");
            }
            if (keystore.isCertificateEntry(alias)) {
                types.add("certificateEntry");
            }
            if (types.isEmpty()) {
                types.add("unknownTypeEntry");
            }
            return String.format("using alias=%s: client %s: %s", alias, StringJoiner.join("+", types), CertificateUtils.getCertificateInfo(cert));
        } catch (final KeyStoreException e) {
            throw new InvalidKeystoreException(e);
        }
    }
}
