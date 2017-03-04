package cz.tomasdvorak.eet.client.logging;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

/**
 * Utility for logging webservice requests and responses to one logger, which can be then redirected or configured
 * in log4j configuration file.
 */
public class WebserviceLogging {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WebserviceLogging.class);

    public static final LoggingOutInterceptor LOGGING_OUT_INTERCEPTOR = new LoggingOutInterceptor() {
        @Override
        protected void log(final Logger ignored, final String message) throws Fault {
            logger.info(message);
        }
    };

    public static final LoggingInInterceptor LOGGING_IN_INTERCEPTOR = new LoggingInInterceptor() {
        @Override
        protected void log(final Logger ignored, final String message) throws Fault {
            logger.info(message);
        }
    };
}
