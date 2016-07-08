package cz.tomasdvorak.eet.client.dto;

public enum  CommunicationMode {
    REAL(false),
    TEST(true);

    private final boolean isCheckOnly;

    CommunicationMode(final boolean isCheckOnly) {
        this.isCheckOnly = isCheckOnly;
    }

    public boolean isCheckOnly() {
        return isCheckOnly;
    }
}
