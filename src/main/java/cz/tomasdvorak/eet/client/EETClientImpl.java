package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.*;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.config.SubmissionType;
import cz.tomasdvorak.eet.client.dto.ResponseCallback;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.dto.WebserviceConfiguration;
import cz.tomasdvorak.eet.client.exceptions.CommunicationException;
import cz.tomasdvorak.eet.client.exceptions.CommunicationTimeoutException;
import cz.tomasdvorak.eet.client.exceptions.DataSigningException;
import cz.tomasdvorak.eet.client.security.ClientKey;
import cz.tomasdvorak.eet.client.security.SecureEETCommunication;
import cz.tomasdvorak.eet.client.security.SecurityCodesGenerator;
import cz.tomasdvorak.eet.client.security.ServerKey;
import cz.tomasdvorak.eet.client.utils.ExceptionUtils;
import org.apache.logging.log4j.Logger;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;


class EETClientImpl extends SecureEETCommunication implements EETClient {

    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(SecureEETCommunication.class);


    EETClientImpl(final ClientKey clientKey, final ServerKey serverKey, final WebserviceConfiguration wsConfiguration) {
        super(clientKey, serverKey, wsConfiguration);
    }

    public SubmitResult submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType) throws DataSigningException, CommunicationException {
        final TrzbaType request = prepareData(receipt, mode, submissionType);
        final EET port = getPort(mode, endpointType);

        try {
            final OdpovedType response = port.odeslaniTrzby(request);
            if (response != null && !response.getVarovani().isEmpty()) {
                for (final OdpovedVarovaniType warning : response.getVarovani()) {
                    logger.warn("Response warning: code=" + warning.getKodVarov() + "; message=" + warning.getContent());
                }
            }
            return new SubmitResult(request, response);
        } catch (final Exception e) {
            if(ExceptionUtils.containsExceptionType(e, SocketTimeoutException.class)) {
                throw new CommunicationTimeoutException(request, e);
            }
            throw new CommunicationException(request, e);
        }
    }



    public Future<?> submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType, final ResponseCallback handler) throws DataSigningException {
        final TrzbaType request = prepareData(receipt, mode, submissionType);
        final EET port = getPort(mode, endpointType);
        return port.odeslaniTrzbyAsync(request, new AsyncHandler<OdpovedType>() {
            @Override
            public void handleResponse(final Response<OdpovedType> res) {
                try {
                    final OdpovedType response = res.get();
                    final SubmitResult submitResult = new SubmitResult(request, response);
                    handler.onComplete(submitResult);

                } catch (final Exception e) {
                    if(ExceptionUtils.containsExceptionType(e, SocketTimeoutException.class)) {
                        handler.onTimeout(new CommunicationTimeoutException(request, e));
                    } else {
                        handler.onError(new CommunicationException(request, e));
                    }
                }
            }
        });
    }

    private TrzbaType prepareData(final TrzbaDataType data, final CommunicationMode mode, final SubmissionType submissionType) throws DataSigningException {
        return new TrzbaType()
                .withHlavicka(getHeader(mode, submissionType))
                .withData(data)
                .withKontrolniKody(getCheckCodes(data));

    }

    private TrzbaHlavickaType getHeader(final CommunicationMode mode, final SubmissionType submissionType) {
        return new TrzbaHlavickaType()
                .withUuidZpravy(UUID.randomUUID().toString())
                .withDatOdesl(new Date())
                .withOvereni(mode.isCheckOnly())
                .withPrvniZaslani(submissionType.isFirstSubmission());
    }

    private TrzbaKontrolniKodyType getCheckCodes(final TrzbaDataType data) throws DataSigningException {
        final SecurityCodesGenerator securityCodesGenerator = new SecurityCodesGenerator(getClientKey());

        final byte[] pkpValue = securityCodesGenerator.getPKP(data);
        final PkpElementType pkp = new PkpElementType()
                .withValue(pkpValue)
                .withCipher(PkpCipherType.RSA_2048)
                .withDigest(PkpDigestType.SHA_256)
                .withEncoding(PkpEncodingType.BASE_64);

        final BkpElementType bkp = new BkpElementType()
                .withValue(securityCodesGenerator.getBKP(pkpValue))
                .withDigest(BkpDigestType.SHA_1)
                .withEncoding(BkpEncodingType.BASE_16);

        return new TrzbaKontrolniKodyType()
                .withPkp(pkp)
                .withBkp(bkp);
    }
}
