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

    TrzbaType prepareRequest(final TrzbaDataType receiptData, final CommunicationMode mode, final SubmissionType submissionType) throws DataSigningException;
    SubmitResult sendSync(final TrzbaType request, final EndpointType endpointType) throws CommunicationException;
    Future<?> sendAsync(final TrzbaType request, final EndpointType endpointType, final ResponseCallback handler);

    /**
     * Central and only method of EET. Send the receipt data to selected endpoint, select if it's first or repeated
     * submission and if the communication is test only or real.
     *
     * This method is DEPRECATED. Prepare data using prepareRequest first, then send them using one of send* methods.
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
     * * This method is DEPRECATED. Prepare data using prepareRequest first, then send them using one of send* methods.
     */
    @Deprecated
    Future<?> submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType, final ResponseCallback handler) throws DataSigningException;
}
