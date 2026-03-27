package com.helloworldconsulting;

import com.google.api.services.youtube.model.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point. Reads configuration from environment variables, picks a random
 * YouTube video from the configured channel, then posts it to all LinkedIn groups.
 *
 * Required environment variables:
 *   LINKEDIN_EMAIL      – LinkedIn account e-mail
 *   LINKEDIN_PASSWORD   – LinkedIn account password
 *   YOUTUBE_API_KEY     – YouTube Data API v3 key
 *
 * Optional:
 *   YOUTUBE_CHANNEL_ID  – defaults to the channel already configured in AppConfig
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfig.fromEnvironment();

        log.info("Fetching random video from channel {}", config.getYoutubeChannelId());
        YouTubeChannelVideos ytService = YouTubeChannelVideos.create(config);
        Video video = ytService.getRandomVideo();

        String videoUrl   = "https://www.youtube.com/watch?v=" + video.getId();
        String videoTitle = video.getSnippet().getTitle();
        log.info("Posting: {} — {}", videoTitle, videoUrl);

        LinkedInPoster poster = LinkedInPoster.create();
        try {
            poster.login(config.getLinkedInEmail(), config.getLinkedInPassword());
            poster.postToGroups(videoUrl, videoTitle);
        } finally {
            poster.quit();
        }
    }
}
