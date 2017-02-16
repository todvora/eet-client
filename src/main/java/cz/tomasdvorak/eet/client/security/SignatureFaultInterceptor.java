package cz.tomasdvorak.eet.client.security;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import cz.etrzby.xml.OdpovedChybaType;
import cz.etrzby.xml.OdpovedType;

/**
 * Interecptor for conditional re-throwing of signature fault.
 * <p>
 * If a signature fault occurred in {@link WSS4JEetInInterceptor}, this
 * interceptor checks the value of {@link OdpovedChybaType#getKod()} and if it
 * was NOT an error response, the signature check fault is real and gets
 * rethrown.
 * <p>
 * Error responses are not signed, therefore a fault should not be thrown.
 * 
 * @author Petr Kalivoda
 *
 */
public class SignatureFaultInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	private static final int ERR_CODE_NO_ERROR = 0;

	public SignatureFaultInterceptor() {
		super(Phase.USER_LOGICAL);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		if (message.getExchange().containsKey(WSS4JEetInInterceptor.PROP_SIGNATURE_ERROR)) {
			MessageContentsList contents = MessageContentsList.getContentsList(message);
			OdpovedType response = (OdpovedType) contents.get(0);
			OdpovedChybaType error = response.getChyba();

			if (error == null || error.getKod() == ERR_CODE_NO_ERROR) {
				throw (Fault) message.getExchange().get(WSS4JEetInInterceptor.PROP_SIGNATURE_ERROR);
			}
		}
	}

}