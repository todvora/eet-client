package cz.tomasdvorak.eet.client.exceptions;


public class ResponseWithErrorException extends Exception {

    private final String errorContent;
    private final int errorCode;

    public ResponseWithErrorException(final int errorCode, final String message, final String errorContent) {
        super(String.format("Error %d: %s (%s)", errorCode, message, errorContent));
        this.errorCode = errorCode;
        this.errorContent = errorContent;
    }

    public String getErrorContent() {
        return errorContent;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
