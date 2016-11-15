package cz.tomasdvorak.eet.client.timing;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Persist a timestamp of {@link Phase#PREPARE_SEND} in every message exchange. This timestamp is later
 * read by {@link TimingReceiveInterceptor} to log a request-response duration.
 */
public class TimingSendInterceptor extends AbstractPhaseInterceptor<Message> {

    public static final TimingSendInterceptor INSTANCE = new TimingSendInterceptor();
    static final String KEY = "eet.timing.start";

    private TimingSendInterceptor() {
        super(Phase.PREPARE_SEND);
    }

    @Override
    public void handleMessage(final Message msg) throws Fault {
        final long startTime = System.currentTimeMillis();
        msg.getExchange().put(KEY, startTime);
    }
}
