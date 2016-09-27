package cz.tomasdvorak.eet.client.exceptions;

/**
 * Revocation lists cannot be obtained or processed. CRLs are part of the EET server certificate, used to sign valid
 * responses. This application is taking the CRLs from the cert on the fly and tries to validate the cert against them.
 *
 * If anything fails, this exception is thrown.
 *
 * This is a recoverable exception and may be caused for example by problems with connection to certificate authority servers.
 */
public class RevocationListException extends Exception {

    public RevocationListException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RevocationListException(final Throwable cause) {
        super(cause);
    }
}
