package cz.tomasdvorak.eet.client.security;

import java.util.Map;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import cz.etrzby.xml.TrzbaHlavickaType;
import cz.etrzby.xml.TrzbaType;

/**
 * Specialization of {@link WSS4JOutInterceptor} that uses exchange to mark
 * messages that have to be validated by {@link WSS4JEetInInterceptor}
 *
 * @author Petr Kalivoda
 *
 */
public class WSS4JEetOutInterceptor extends WSS4JOutInterceptor {

	public WSS4JEetOutInterceptor(Map<String, Object> properties) {
		super(properties);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		super.handleMessage(message);

		MessageContentsList contents = MessageContentsList.getContentsList(message);
		if (contents != null && contents.size() == 1) {
			Object requestObj = contents.get(0);
			if (requestObj instanceof TrzbaType) {
				TrzbaType request = (TrzbaType) requestObj;
				TrzbaHlavickaType header = request.getHlavicka();

				// validation is required if getOvereni is unspecified or false.
				boolean required = header == null || !Boolean.TRUE.equals(header.getOvereni());
				message.getExchange().put(WSS4JEetInInterceptor.PROP_SIGNATURE_REQUIRED, required);
			}
		}

	}

}
