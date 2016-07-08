package cz.tomasdvorak.eet.client.dto;

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
