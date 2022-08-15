package com.silouks.infoly.artists.service;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@SpringBootTest()
public class ArtistsHandlerTest {
    private MockWebServer mockWebServer;
    private ArtistsHandler artistsHandler;

    @BeforeEach
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = "http://" + mockWebServer.getHostName() + ":" + mockWebServer.getPort() + "/";
        var iTunesService = new ITunesService(WebClient.builder().baseUrl(baseUrl).build());
        var cachedITunesService = new CachedITunesService(iTunesService);
        artistsHandler = new ArtistsHandler(cachedITunesService);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void test_searchArtists_returns_400_without_term() throws Exception {
        var requestBuilder = MockServerRequest.builder();
        var response = artistsHandler.searchArtists(requestBuilder.build()).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
    }

    @Test
    public void test_lookUpArtists_returns_400_without_id() throws Exception {
        var requestBuilder = MockServerRequest.builder();
        var response = artistsHandler.lookUpArtist(requestBuilder.build()).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
    }

    @Test
    public void test_lookUpArtists_returns_400_when_id_is_string() throws Exception {
        var requestBuilder = MockServerRequest.builder();
        requestBuilder.pathVariable("id", "test");
        var response = artistsHandler.lookUpArtist(requestBuilder.build()).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
    }

}

