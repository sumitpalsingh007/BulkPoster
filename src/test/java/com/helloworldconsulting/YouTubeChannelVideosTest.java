package com.helloworldconsulting;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YouTubeChannelVideosTest {

    // RETURNS_DEEP_STUBS lets us mock long fluent chains like
    //   youtube.search().list("id").execute()
    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    YouTube mockYouTube;

    private static final String CHANNEL_ID = "UCtest123";
    private static final String API_KEY    = "test-api-key";

    private YouTubeChannelVideos service;

    @BeforeEach
    void setUp() {
        service = new YouTubeChannelVideos(mockYouTube, CHANNEL_ID, API_KEY);
    }

    @Test
    void getRandomVideoReturnsVideoFromChannel() throws IOException {
        SearchListResponse searchResponse = buildSearchResponse("vid1", "vid2");
        when(mockYouTube.search().list(anyString()).execute()).thenReturn(searchResponse);

        VideoListResponse videoResponse = buildVideoResponse("vid1", "First Video", 1000L);
        when(mockYouTube.videos().list(anyString()).execute()).thenReturn(videoResponse);

        Video result = service.getRandomVideo();

        assertNotNull(result);
        assertEquals("vid1", result.getId());
        assertEquals("First Video", result.getSnippet().getTitle());
    }

    @Test
    void getChannelVideosReturnsAllVideos() throws IOException {
        SearchListResponse searchResponse = buildSearchResponse("a", "b", "c");
        when(mockYouTube.search().list(anyString()).execute()).thenReturn(searchResponse);

        VideoListResponse videoResponse = buildVideoResponse("a", "Video A", 500L);
        when(mockYouTube.videos().list(anyString()).execute()).thenReturn(videoResponse);

        List<Video> videos = service.getChannelVideos();

        assertFalse(videos.isEmpty());
    }

    @Test
    void getRandomVideoThrowsWhenChannelIsEmpty() throws IOException {
        SearchListResponse emptyResponse = new SearchListResponse();
        emptyResponse.setItems(List.of());
        when(mockYouTube.search().list(anyString()).execute()).thenReturn(emptyResponse);

        VideoListResponse emptyVideoResponse = new VideoListResponse();
        emptyVideoResponse.setItems(List.of());
        when(mockYouTube.videos().list(anyString()).execute()).thenReturn(emptyVideoResponse);

        assertThrows(IllegalStateException.class, () -> service.getRandomVideo());
    }

    @Test
    void getChannelVideosPaginatesThroughAllPages() throws IOException {
        // First page has a next-page token; second page does not
        SearchListResponse page1 = buildSearchResponse("v1");
        page1.setNextPageToken("page2token");

        SearchListResponse page2 = buildSearchResponse("v2");
        // nextPageToken is null by default → pagination stops

        when(mockYouTube.search().list(anyString()).execute())
            .thenReturn(page1)
            .thenReturn(page2);

        VideoListResponse videoResponse = buildVideoResponse("v1", "Video 1", 100L);
        when(mockYouTube.videos().list(anyString()).execute()).thenReturn(videoResponse);

        service.getChannelVideos();

        // search().list().execute() should have been called twice (once per page)
        verify(mockYouTube.search().list(anyString()), times(2)).execute();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static SearchListResponse buildSearchResponse(String... videoIds) {
        SearchListResponse response = new SearchListResponse();
        response.setItems(
            java.util.Arrays.stream(videoIds)
                .map(id -> {
                    SearchResult sr = new SearchResult();
                    ResourceId rid = new ResourceId();
                    rid.setVideoId(id);
                    sr.setId(rid);
                    return sr;
                })
                .collect(java.util.stream.Collectors.toList())
        );
        return response;
    }

    private static VideoListResponse buildVideoResponse(String id, String title, long views) {
        Video video = new Video();
        video.setId(id);

        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(title);
        video.setSnippet(snippet);

        VideoStatistics stats = new VideoStatistics();
        stats.setViewCount(BigInteger.valueOf(views));
        video.setStatistics(stats);

        VideoListResponse response = new VideoListResponse();
        response.setItems(List.of(video));
        return response;
    }
}
