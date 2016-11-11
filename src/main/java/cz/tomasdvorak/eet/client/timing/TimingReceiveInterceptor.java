package cz.tomasdvorak.eet.client.timing;

import cz.tomasdvorak.eet.client.utils.DateUtils;
import cz.tomasdvorak.eet.client.utils.StringJoiner;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Date;

public class TimingReceiveInterceptor extends AbstractPhaseInterceptor<Message> {

    public static final TimingReceiveInterceptor INSTANCE = new TimingReceiveInterceptor();

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(TimingReceiveInterceptor.class);

    public TimingReceiveInterceptor() {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(final Message msg) throws Fault {
        final Long startTime = (Long) msg.getExchange().remove(TimingSendInterceptor.KEY);
        if (startTime != null) {
            final long executionTime = System.currentTimeMillis() - startTime;
            logger.info(StringJoiner.join(";", Arrays.asList(
                    "" + DateUtils.format(new Date()),
                    "" + executionTime,
                    "" + msg.getExchange().get(Message.ENDPOINT_ADDRESS),
                    "id_" + msg.getExchange().get(LoggingMessage.ID_KEY)
                    )
            ));
        }
    }
}