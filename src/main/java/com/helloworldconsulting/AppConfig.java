package com.helloworldconsulting;

public class AppConfig {

    private static final String DEFAULT_CHANNEL_ID = "UC1ZB_M18rLYUzmsdPEeoXnA";

    private final String linkedInEmail;
    private final String linkedInPassword;
    private final String youtubeApiKey;
    private final String youtubeChannelId;

    public AppConfig(String linkedInEmail, String linkedInPassword,
                     String youtubeApiKey, String youtubeChannelId) {
        this.linkedInEmail = linkedInEmail;
        this.linkedInPassword = linkedInPassword;
        this.youtubeApiKey = youtubeApiKey;
        this.youtubeChannelId = youtubeChannelId;
    }

    /**
     * Loads configuration from environment variables.
     * Required: LINKEDIN_EMAIL, LINKEDIN_PASSWORD, YOUTUBE_API_KEY
     * Optional: YOUTUBE_CHANNEL_ID (defaults to the channel already configured)
     */
    public static AppConfig fromEnvironment() {
        return new AppConfig(
            requireEnv("LINKEDIN_EMAIL"),
            requireEnv("LINKEDIN_PASSWORD"),
            requireEnv("YOUTUBE_API_KEY"),
            System.getenv().getOrDefault("YOUTUBE_CHANNEL_ID", DEFAULT_CHANNEL_ID)
        );
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Required environment variable not set: " + name);
        }
        return value;
    }

    public String getLinkedInEmail()    { return linkedInEmail; }
    public String getLinkedInPassword() { return linkedInPassword; }
    public String getYoutubeApiKey()    { return youtubeApiKey; }
    public String getYoutubeChannelId() { return youtubeChannelId; }
}
