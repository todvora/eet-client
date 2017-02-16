package cz.tomasdvorak.eet.client.utils;

public class ExceptionUtils {
    public static boolean containsExceptionType(Throwable exception, Class<? extends Exception> awaitedType) {

        if (exception != null) {
            if (awaitedType.isAssignableFrom(exception.getClass())) {
                return true;
            } else {
                return containsExceptionType(exception.getCause(), awaitedType);
            }
        }
        return false;
    }
}
