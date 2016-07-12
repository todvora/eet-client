package cz.tomasdvorak.eet.client.config;

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
