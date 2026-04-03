package com.helloworldconsulting;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class YouTubeChannelVideos {

    private static final Logger log = LoggerFactory.getLogger(YouTubeChannelVideos.class);
    private static final String APPLICATION_NAME = "Bulk Poster";

    private final YouTube youtubeService;
    private final String channelId;
    private final String apiKey;

    // Package-private for unit testing with a mock YouTube service
    YouTubeChannelVideos(YouTube youtubeService, String channelId, String apiKey) {
        this.youtubeService = youtubeService;
        this.channelId = channelId;
        this.apiKey = apiKey;
    }

    public static YouTubeChannelVideos create(AppConfig config)
            throws GeneralSecurityException, IOException {
        YouTube service = new YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            null)
            .setApplicationName(APPLICATION_NAME)
            .build();
        return new YouTubeChannelVideos(service, config.getYoutubeChannelId(), config.getYoutubeApiKey());
    }

    /**
     * Returns a random video from the configured channel.
     */
    public Video getRandomVideo() throws IOException {
        List<Video> videos = getChannelVideos();
        if (videos.isEmpty()) {
            throw new IllegalStateException("No videos found in channel: " + channelId);
        }
        Video video = videos.get(new Random().nextInt(videos.size()));
        log.info("Selected random video: {} ({})", video.getSnippet().getTitle(), video.getId());
        return video;
    }

    /**
     * Fetches all videos from the channel, paginating through all results.
     */
    public List<Video> getChannelVideos() throws IOException {
        List<String> videoIds = fetchVideoIds();
        return fetchVideoDetails(videoIds);
    }

    private List<String> fetchVideoIds() throws IOException {
        List<String> videoIds = new ArrayList<>();

        YouTube.Search.List searchRequest = youtubeService.search().list("id");
        searchRequest.setChannelId(channelId);
        searchRequest.setMaxResults(50L);
        searchRequest.setOrder("date");
        searchRequest.setType("video");
        searchRequest.setKey(apiKey);

        SearchListResponse response = searchRequest.execute();
        collectVideoIds(response, videoIds);

        while (response.getNextPageToken() != null) {
            searchRequest.setPageToken(response.getNextPageToken());
            response = searchRequest.execute();
            collectVideoIds(response, videoIds);
        }

        log.info("Found {} videos in channel {}", videoIds.size(), channelId);
        return videoIds;
    }

    private void collectVideoIds(SearchListResponse response, List<String> videoIds) {
        for (SearchResult result : response.getItems()) {
            videoIds.add(result.getId().getVideoId());
        }
    }

    private List<Video> fetchVideoDetails(List<String> videoIds) throws IOException {
        List<Video> videos = new ArrayList<>();

        for (int i = 0; i < videoIds.size(); i += 50) {
            int end = Math.min(i + 50, videoIds.size());
            List<String> batch = videoIds.subList(i, end);

            YouTube.Videos.List videosRequest = youtubeService.videos().list("id,snippet,statistics");
            videosRequest.setId(String.join(",", batch));
            videosRequest.setKey(apiKey);

            VideoListResponse response = videosRequest.execute();
            videos.addAll(response.getItems());
        }

        return videos;
    }
}
