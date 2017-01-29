package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaType;
import cz.tomasdvorak.eet.client.config.CommunicationMode;
import cz.tomasdvorak.eet.client.config.EndpointType;
import cz.tomasdvorak.eet.client.config.SubmissionType;
import cz.tomasdvorak.eet.client.dto.ResponseCallback;
import cz.tomasdvorak.eet.client.dto.SubmitResult;
import cz.tomasdvorak.eet.client.exceptions.DataSigningException;
import cz.tomasdvorak.eet.client.exceptions.CommunicationException;

import java.util.concurrent.Future;

/**
 * EET client implementation, handling computation of security codes, signing of requests, validation of responses.
 */
public interface EETClient {

    /**
     * Prepare request for <strong>first submit</strong> to EET Service.
     * @param receiptData Receipt data like price, VAT or Tax ID.
     * @param mode test or real communication (will be encoded in header
     * @return complete request, including computed security codes, added random message UUID, current date.
     * @throws DataSigningException if problem with signing occurs. Should be related to client key in that case
     */
    TrzbaType prepareFirstRequest(final TrzbaDataType receiptData, final CommunicationMode mode) throws DataSigningException;

    /**
     * Prepare request for <strong>second or every other</strong> submit to EET service.
     * @param request the original request, which has been already constructed and sent to EET servers.
     * @return Updated request, re-generated message UUID, send date, set first-submission flag to false, recomputed
     * security codes if they change meanwhile (new client certificate used for this submission)
     * @throws DataSigningException if problem with signing occurs. Should be related to client key in that case
     */
    TrzbaType prepareRepeatedRequest(final TrzbaType request) throws DataSigningException;

    /**
     * Submit synchronously a receipt to EET servers.
     * @param request prepared by calling one of {@link #prepareFirstRequest(TrzbaDataType, CommunicationMode)} or {@link #prepareRepeatedRequest(TrzbaType)} method.
     * @param endpointType real or test endpoint
     * @return result provided by EET servers.
     * @throws CommunicationException if something fails. It may be timeout, not accessible servers, invalid message, invalid signature of response.
     */
    SubmitResult sendSync(final TrzbaType request, final EndpointType endpointType) throws CommunicationException;

    /**
     * Submit asynchronously a receipt to EET servers.
     * @param request prepared by calling one of {@link #prepareFirstRequest(TrzbaDataType, CommunicationMode)} or {@link #prepareRepeatedRequest(TrzbaType)} method.
     * @param endpointType real or test endpoint
     * @param handler callback for response or failure of the call
     * @return result future you can wait on
     */
    Future<?> sendAsync(final TrzbaType request, final EndpointType endpointType, final ResponseCallback handler);

    /**
     * Central method of EET. Send the receipt data to selected endpoint, select if it's first or repeated
     * submission and if the communication is test only or real.
     *
     * This method is DEPRECATED. Prepare data using prepareFirstRequest first, then send them using one of send* methods.
     *
     * @param receipt Receipt data - price, date, tax numbers, ...
     * @param mode real or test submission
     * @param endpointType playground or real endpoint
     * @param submissionType first or repeated submission
     * @return EET response with added request needed to access security codes and other data in case of failed submission
     * @throws DataSigningException Failed to compute PKP or BKP
     * @throws CommunicationException Failed to send or receive data from EET endpoint
     */
    @Deprecated
    SubmitResult submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType) throws DataSigningException, CommunicationException;

    /**
     * * This method is DEPRECATED. Prepare data using prepareFirstRequest first, then send them using one of send* methods.
     */
    @Deprecated
    Future<?> submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType, final ResponseCallback handler) throws DataSigningException;
}
