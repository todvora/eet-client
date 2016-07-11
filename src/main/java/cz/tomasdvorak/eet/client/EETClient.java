package cz.tomasdvorak.eet.client;

import cz.etrzby.xml.OdpovedType;
import cz.etrzby.xml.TrzbaDataType;
import cz.tomasdvorak.eet.client.dto.CommunicationMode;
import cz.tomasdvorak.eet.client.dto.EndpointType;
import cz.tomasdvorak.eet.client.dto.SubmissionType;
import cz.tomasdvorak.eet.client.exceptions.DataSigningException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

interface EETClient {
    OdpovedType submitReceipt(final TrzbaDataType receipt, final CommunicationMode mode, final EndpointType endpointType, final SubmissionType submissionType) throws DataSigningException;
}
