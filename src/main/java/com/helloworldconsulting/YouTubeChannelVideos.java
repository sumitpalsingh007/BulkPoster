package com.helloworldconsulting;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

public class YouTubeChannelVideos {

    private static final String APPLICATION_NAME = "Bulk Poster";
    private static final String API_KEY = "";
    private static final String CHANNEL_ID = "UC1ZB_M18rLYUzmsdPEeoXnA";

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();
        String channelId = "UC1ZB_M18rLYUzmsdPEeoXnA";
        List<Video> videos = getChannelVideos(youtubeService, channelId);

        videos.sort((v1, v2) -> Long.compare(v2.getStatistics().getViewCount().longValue(),
                                             v1.getStatistics().getViewCount().longValue()));

        for (Video video : videos) {
            System.out.println("Title: " + video.getSnippet().getTitle()
                               + ", Views: " + video.getStatistics().getViewCount()
                               + ", Kind: " + video.getKind());
            System.out.println("Link: https://www.youtube.com/watch?v=" + video.getId());
        }
    }

    public static Video getRandomYoutubeVideos() throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();
        List<Video> videos = getChannelVideos(youtubeService, CHANNEL_ID);
        Random random = new Random();
        Video randomVideo = videos.get(random.nextInt(videos.size()));
        System.out.println("Title: " + randomVideo.getSnippet().getTitle()
                           + ", Views: " + randomVideo.getStatistics().getViewCount()
                           + ", Kind: " + randomVideo.getKind());
        System.out.println("Link: https://www.youtube.com/watch?v=" + randomVideo.getId());
        return randomVideo;
    }

    public static YouTube getService() throws GeneralSecurityException, IOException {
        return new YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            null)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    public static List<Video> getChannelVideos(YouTube youtubeService, String channelId) throws IOException {
        List<Video> videoList = new ArrayList<>();

        YouTube.Search.List searchRequest = youtubeService.search().list("id");
        searchRequest.setChannelId(channelId);
        searchRequest.setMaxResults(50L); // Maximum allowed is 50
        searchRequest.setOrder("date"); // Get the most recent videos first
        searchRequest.setType("video");
        searchRequest.setKey(API_KEY);

        SearchListResponse searchResponse = searchRequest.execute();
        List<SearchResult> searchResults = searchResponse.getItems();

        while (searchResponse.getNextPageToken() != null) {
            searchRequest.setPageToken(searchResponse.getNextPageToken());
            searchResponse = searchRequest.execute();
            searchResults.addAll(searchResponse.getItems());
        }

        List<String> videoIds = new ArrayList<>();
        for (SearchResult searchResult : searchResults) {
            videoIds.add(searchResult.getId().getVideoId());
        }

        // Fetch video details in batches of 50 (API limit)
        for (int i = 0; i < videoIds.size(); i += 50) {
            int end = Math.min(i + 50, videoIds.size());
            List<String> subList = videoIds.subList(i, end);

            YouTube.Videos.List videosRequest = youtubeService.videos().list("id,snippet,statistics");
            videosRequest.setId(String.join(",", subList));
            videosRequest.setKey(API_KEY);

            VideoListResponse videosResponse = videosRequest.execute();
            videoList.addAll(videosResponse.getItems());
        }

        return videoList;
    }
}
