package cz.tomasdvorak.eet.client.timing;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class TimingSendInterceptor extends AbstractPhaseInterceptor<Message> {

    public static final TimingSendInterceptor INSTANCE = new TimingSendInterceptor();
    public static final String KEY = "eet.timing.start";

    public TimingSendInterceptor() {
        super(Phase.PREPARE_SEND);
    }

    @Override
    public void handleMessage(final Message msg) throws Fault {
        final long startTime = System.currentTimeMillis();
        msg.getExchange().put(KEY, startTime);
    }
}
