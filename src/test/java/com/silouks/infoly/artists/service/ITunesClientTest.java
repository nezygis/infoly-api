package com.silouks.infoly.artists.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silouks.infoly.artists.model.AlbumDTO;
import com.silouks.infoly.artists.model.ArtistLookupDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static com.silouks.infoly.artists.service.ITunesClient.ALBUM_WRAPPER_TYPE;

@SpringBootTest()
public class ITunesClientTest {
    private MockWebServer mockWebServer;
    private ITunesClient iTunesClient;

    @BeforeEach
    public void setup() {
        mockWebServer = new MockWebServer();
        String baseUrl = "http://" + mockWebServer.getHostName() + ":" + mockWebServer.getPort() + "/";
        iTunesClient = new ITunesClient(WebClient.builder().baseUrl(baseUrl).build());
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void test_lookUpArtist_filtersNonAlbumTypes() throws Exception {
        var artistLookupDTO = ArtistLookupDTO
                .builder()
                .results(List.of(
                        AlbumDTO.builder().wrapperType(ALBUM_WRAPPER_TYPE).build(),
                        AlbumDTO.builder().wrapperType("other type").build()
                ))
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(artistLookupDTO))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        var response = iTunesClient.lookUpArtist(123).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getResults().size());
    }
}

