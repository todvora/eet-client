package cz.tomasdvorak.eet.client.config;

import cz.etrzby.xml.TrzbaDataType;
import cz.etrzby.xml.TrzbaType;

/**
 * The type of submission will be automatically set depending on the selected {@link cz.tomasdvorak.eet.client.EETClient#prepareFirstRequest(TrzbaDataType, CommunicationMode)}
 * or {@link cz.tomasdvorak.eet.client.EETClient#prepareRepeatedRequest(TrzbaType)} method call.
 */
@Deprecated
public enum SubmissionType {
    FIRST_ATTEMPT(true),
    REPEATED_ATTEMPT(false);

    private final boolean isFirstSubmission;

    SubmissionType(final boolean isFirstSubmission) {
        this.isFirstSubmission = isFirstSubmission;
    }

    public boolean isFirstSubmission() {
        return isFirstSubmission;
    }
}
