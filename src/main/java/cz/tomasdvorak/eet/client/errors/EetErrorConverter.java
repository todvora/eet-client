package cz.tomasdvorak.eet.client.errors;

import cz.etrzby.xml.OdpovedChybaType;
import cz.tomasdvorak.eet.client.exceptions.ResponseWithErrorException;

public class EetErrorConverter {
    public static ResponseWithErrorException getErrorType(final OdpovedChybaType errorResponse) {

        if (errorResponse == null) {
            return null;
        }

        final int errorCode = errorResponse.getKod();
        try {
            final EetErrorType eetErrorType = EetErrorType.byErrorCode(errorCode);
            if (Boolean.TRUE.equals(errorResponse.isTest()) && eetErrorType == EetErrorType.VERIFICATION_MODE_SUCCESS) {
                // is success, do nothing
                return null;
            }
            return new ResponseWithErrorException(eetErrorType.getErrorCode(), eetErrorType.getMessage(), errorResponse.getContent());
        } catch (IllegalArgumentException e) {
            // unknown err type!
            return new ResponseWithErrorException(errorResponse.getKod(), "Unknown EET error", errorResponse.getContent());
        }
    }
}
