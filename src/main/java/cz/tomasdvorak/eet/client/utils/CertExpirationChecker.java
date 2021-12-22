package cz.tomasdvorak.eet.client.utils;

import cz.tomasdvorak.eet.client.exceptions.InvalidKeystoreException;
import org.slf4j.Logger;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CertExpirationChecker {

    private final X509Certificate certificate;
    private long duration;
    private TimeUnit timeUnit;
    private Date compareAgainstDate;

    private CertExpirationChecker(X509Certificate certificate) {
        this.certificate = certificate;
        this.compareAgainstDate = new Date();
    }

    public static CertExpirationChecker of(X509Certificate certificate) {
        return new CertExpirationChecker(certificate);
    }

    public static CertExpirationChecker of(KeyStore keystore, String alias) throws InvalidKeystoreException {
        try {
            final X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
            assert cert != null;
            return new CertExpirationChecker(cert);
        } catch (KeyStoreException e) {
            throw new InvalidKeystoreException(e);
        }
    }

    public CertExpirationChecker whenExpiresIn(long duration, TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;
        return this;
    }

    public void printWarningTo(Logger logger) {
        if(willExpireSoon()) {
            logger.warn(formatWarningMessage());
        }
    }

    private boolean willExpireSoon() {
        return getDaysToExpiration() < timeUnit.toDays(duration);
    }

    private String formatWarningMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n").append("#### WARNING ####").append("\n");
        builder.append(String.format("Following certificate expires on %s!", DateUtils.format(certificate.getNotAfter()))).append("\n");
        builder.append(CertificateUtils.getCertificateInfo(certificate)).append("\n");
        builder.append("Please update your certificate as soon as possible. More info on https://github.com/todvora/eet-client#certificate-expiration").append("\n");
        builder.append("##################");
        return builder.toString();
    }

    protected long getDaysToExpiration() {
        Date notAfter = this.certificate.getNotAfter();
        long toExpiration = notAfter.getTime() - compareAgainstDate.getTime();
        return TimeUnit.MILLISECONDS.toDays(toExpiration);
    }

    protected CertExpirationChecker withCompareAgainstDate(final Date compareAgainstDate) {
        this.compareAgainstDate = compareAgainstDate;
        return this;
    }
}
