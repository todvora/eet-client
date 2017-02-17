package cz.tomasdvorak.eet.client.timing;

import cz.tomasdvorak.eet.client.utils.DateUtils;
import cz.tomasdvorak.eet.client.utils.StringJoiner;
import java.time.ZonedDateTime;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Interceptor for logging the request-responce cycle duration to a CSV log.
 */
public class TimingReceiveInterceptor extends AbstractPhaseInterceptor<Message> {

    public static final TimingReceiveInterceptor INSTANCE = new TimingReceiveInterceptor();

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(TimingReceiveInterceptor.class);

    private TimingReceiveInterceptor() {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(final Message msg) throws Fault {
        final Long startTime = (Long) msg.getExchange().remove(TimingSendInterceptor.KEY);
        if (startTime != null) {
            final long executionTime = System.currentTimeMillis() - startTime;
            logger.info(formatLogEntry(msg, executionTime));
        }
    }

    private String formatLogEntry(final Message msg, final long executionTime) {
        return StringJoiner.join(";", Arrays.asList(
                "" + DateUtils.format(ZonedDateTime.now()),
                "" + executionTime,
                "" + msg.getExchange().get(Message.ENDPOINT_ADDRESS),
                "id_" + msg.getExchange().get(LoggingMessage.ID_KEY)
        )
        );
    }
}
