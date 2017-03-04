package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.*;
import cz.tomasdvorak.eet.client.concurency.CompletedFuture;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.config.SubmissionType;
import cz.tomasdvorak.eet.client.dto.ResponseCallback;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.dto.WebserviceConfiguration;
import cz.tomasdvorak.eet.client.errors.EetErrorConverter;
import cz.tomasdvorak.eet.client.exceptions.*;
import cz.tomasdvorak.eet.client.security.ClientKey;
import cz.tomasdvorak.eet.client.security.SecureEETCommunication;
import cz.tomasdvorak.eet.client.security.SecurityCodesGenerator;
import cz.tomasdvorak.eet.client.security.ServerKey;
import cz.tomasdvorak.eet.client.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;


class EETClientImpl extends SecureEETCommunication implements EETClient {

    private static final Logger logger = LoggerFactory.getLogger(EETClientImpl.class);


    EETClientImpl(final ClientKey clientKey, final ServerKey serverKey, final WebserviceConfiguration wsConfiguration) {
        super(clientKey, serverKey, wsConfiguration);
    }

    @Deprecated
    public SubmitResult submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType) throws DataSigningException, CommunicationException {
        final TrzbaType request = prepareRequest(receipt, mode, submissionType);
        return sendSync(request, endpointType);
    }

    @Deprecated
    public Future<?> submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType, final ResponseCallback handler) throws DataSigningException {
        final TrzbaType request = prepareRequest(receipt, mode, submissionType);
        return sendAsync(request, endpointType, handler);
    }

    public SubmitResult sendSync(final TrzbaType request, final EndpointType endpointType) throws CommunicationException {
        final EET port;
        try {
            port = getPort(endpointType);
        } catch (DnsLookupFailedException e) {
            throw new CommunicationException(request, e);
        } catch (DnsTimeoutException e) {
            throw new CommunicationTimeoutException(request, e);
        }
        try {
            final OdpovedType response = port.odeslaniTrzby(request);
            return convertToSubmitResult(request, response);
        } catch (final Exception e) {
            if(ExceptionUtils.containsExceptionType(e, SocketTimeoutException.class)) {
                throw new CommunicationTimeoutException(request, e);
            }
            throw new CommunicationException(request, e);
        }
    }

    public Future<?> sendAsync(final TrzbaType request, final EndpointType endpointType, final ResponseCallback handler) {
        final EET port;
        try {
            port = getPort(endpointType);
            return port.odeslaniTrzbyAsync(request, new AsyncHandler<OdpovedType>() {
                @Override
                public void handleResponse(final Response<OdpovedType> res) {
                    try {
                        final OdpovedType response = res.get();
                        final SubmitResult submitResult = convertToSubmitResult(request, response);
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
        } catch (DnsLookupFailedException e) {
            handler.onError(new CommunicationException(request, e));
        } catch (DnsTimeoutException e) {
            handler.onError(new CommunicationTimeoutException(request, e));
        }
        return new CompletedFuture<Object>();
    }

    private SubmitResult convertToSubmitResult(final TrzbaType request, final OdpovedType response) throws ResponseWithErrorException {
        if (response != null && !response.getVarovani().isEmpty()) {
            for (final OdpovedVarovaniType warning : response.getVarovani()) {
                logger.warn("Response warning: code=" + warning.getKodVarov() + "; message=" + warning.getContent());
            }
        }
        if(response != null) {
            final ResponseWithErrorException error = EetErrorConverter.getErrorType(response.getChyba());
            if(error != null) {
                throw error;
            }
        }
        return new SubmitResult(request, response);
    }

    public TrzbaType prepareFirstRequest(final TrzbaDataType receiptData, final CommunicationMode mode) throws DataSigningException {
        return prepareRequest(receiptData, mode, SubmissionType.FIRST_ATTEMPT);
    }

    private TrzbaType prepareRequest(final TrzbaDataType receiptData, final CommunicationMode mode, final SubmissionType submissionType) throws DataSigningException {
        return new TrzbaType()
                .withHlavicka(getHeader(mode, submissionType))
                .withData(receiptData)
                .withKontrolniKody(getCheckCodes(receiptData));
    }

    @Override
    public TrzbaType prepareRepeatedRequest(final TrzbaType request) throws DataSigningException {
        request.getHlavicka().setUuidZpravy(UUID.randomUUID().toString());
        request.getHlavicka().setDatOdesl(new Date());
        request.getHlavicka().setPrvniZaslani(false);

        final TrzbaKontrolniKodyType newCheckCodes = getCheckCodes(request.getData());

        if(!request.getKontrolniKody().getBkp().getValue().equals(newCheckCodes.getBkp().getValue()) || !Arrays.equals(request.getKontrolniKody().getPkp().getValue(), newCheckCodes.getPkp().getValue())) {
            logger.warn("Check codes BKP and PKP from original request doesn't corespond with current computed. This can happen for example when client certificate is renewed.");
        }
        return request;
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
