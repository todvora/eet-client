package cz.tomasdvorak.eet.client.errors;

public enum EetErrorType {
    TEMPORARY_TECHNICAL_ERROR(-1, "Temporary technical error in processing - please re-send the data message later"),
    VERIFICATION_MODE_SUCCESS(0, "The registered sale data message in verification mode was successfully processed"),
    INVALID_XML_ENCODING(2, "The XML encoding is not valid"),
    INVALID_SCHEMA_CHECK(3, "The XML message failed the XML schema check"),
    INVALID_SOAP_SIGNATURE(4, "Invalid SOAP message signature"),
    INVALID_BKP_CODE(5, "Invalid Taxpayer's Security Code (BKP)"),
    INVALID_TAX_NUMBER(6, "Invalid structure of tax identification number"),
    MESSAGE_TOO_BIG(7, "The data message is too big"),
    TECHNICAL_OR_DATA_ERROR(8, "The data message was not processed because of a technical error or a data error");

    private final int errorCode;
    private final String message;

    EetErrorType(final int errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public static EetErrorType byErrorCode(final int errorCode) {
        for (EetErrorType type : values()) {
            if (type.getErrorCode() == errorCode) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown error code " + errorCode);
    }
}
