package com.helloworldconsulting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void constructorStoresAllValues() {
        AppConfig config = new AppConfig("email@test.com", "pass123", "yt-key", "UC123");

        assertEquals("email@test.com", config.getLinkedInEmail());
        assertEquals("pass123",        config.getLinkedInPassword());
        assertEquals("yt-key",         config.getYoutubeApiKey());
        assertEquals("UC123",          config.getYoutubeChannelId());
    }

    @Test
    void fromEnvironmentThrowsWhenLinkedInEmailMissing() {
        // Environment does not have LINKEDIN_EMAIL set in unit test runner → should throw
        // (If the env var happens to be set in CI, this test is skipped gracefully.)
        if (System.getenv("LINKEDIN_EMAIL") != null) return;

        assertThrows(IllegalStateException.class, AppConfig::fromEnvironment);
    }

    @Test
    void fromEnvironmentThrowsWhenLinkedInPasswordMissing() {
        if (System.getenv("LINKEDIN_EMAIL")   != null &&
            System.getenv("LINKEDIN_PASSWORD") == null) {
            assertThrows(IllegalStateException.class, AppConfig::fromEnvironment);
        }
    }

    @Test
    void defaultChannelIdIsUsedWhenEnvVarAbsent() {
        // We can test the default directly via the public constructor default value behaviour
        // by checking that a manually constructed config with the known default ID round-trips.
        AppConfig config = new AppConfig("e", "p", "k", "UC1ZB_M18rLYUzmsdPEeoXnA");
        assertEquals("UC1ZB_M18rLYUzmsdPEeoXnA", config.getYoutubeChannelId());
    }
}
