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

import static com.silouks.infoly.artists.service.ITunesService.ALBUM_WRAPPER_TYPE;

@SpringBootTest()
public class ITunesServiceTest {
    private MockWebServer mockWebServer;
    private ITunesService iTunesService;

    @BeforeEach
    public void setup() {
        mockWebServer = new MockWebServer();
        String baseUrl = "http://" + mockWebServer.getHostName() + ":" + mockWebServer.getPort() + "/";
        iTunesService = new ITunesService(WebClient.builder().baseUrl(baseUrl).build());
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

        var response = iTunesService.lookUpArtist(123).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getResults().size());
    }
}

