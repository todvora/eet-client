package cz.tomasdvorak.eet.client.security;

import java.util.Map;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;

/**
 * Interceptor that conditinally validates EET signature based on
 * {@link WSS4JEetOutInterceptor#PROP_SIGNATURE_REQUIRED}.
 * 
 * @author Petr Kalivoda
 *
 */
public class WSS4JEetInInterceptor extends WSS4JInInterceptor {

	protected static final String PROP_SIGNATURE_REQUIRED = "sig.required";
	protected static final String PROP_SIGNATURE_ERROR = "sig.error";

	public WSS4JEetInInterceptor(Map<String, Object> properties) {
		super(properties);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		if (Boolean.TRUE.equals(message.getExchange().get(PROP_SIGNATURE_REQUIRED))) {
			try {
				super.handleMessage(message);
			} catch (Fault f) {
				// don't throw fault directly, body needs to be validated also
				message.getExchange().put(PROP_SIGNATURE_ERROR, f);
			}
		}
	}
}
