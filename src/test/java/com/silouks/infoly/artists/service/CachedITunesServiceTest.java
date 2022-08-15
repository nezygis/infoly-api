package com.silouks.infoly.artists.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silouks.infoly.artists.model.AlbumDTO;
import com.silouks.infoly.artists.model.ArtistDTO;
import com.silouks.infoly.artists.model.ArtistLookupDTO;
import com.silouks.infoly.artists.model.ArtistSearchDTO;
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
public class CachedITunesServiceTest {
    private MockWebServer mockWebServer;
    private CachedITunesService cachedITunesService;

    @BeforeEach
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = "http://" + mockWebServer.getHostName() + ":" + mockWebServer.getPort() + "/";
        cachedITunesService = new CachedITunesService(new ITunesService(WebClient.builder().baseUrl(baseUrl).build()));
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void test_lookUpArtist_cachesResponses() throws Exception {
        ArtistLookupDTO artistLookupDTO = ArtistLookupDTO
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

        for (int i = 0; i < 5; i++) {
            var response = cachedITunesService.lookUpArtist(123).block();
            Assertions.assertNotNull(response);
            Assertions.assertEquals(1, response.getResults().size());
            Assertions.assertEquals(ALBUM_WRAPPER_TYPE, response.getResults().get(0).getWrapperType());
        }

        Assertions.assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    public void test_SearchForArtists_cachesResponses() throws Exception {
        ArtistSearchDTO artistSearchDTO = ArtistSearchDTO
                .builder()
                .results(List.of(
                        ArtistDTO.builder().artistName("Queen").build(),
                        ArtistDTO.builder().artistName("King Crimson").build()
                ))
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(artistSearchDTO))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        for (int i = 0; i < 5; i++) {
            var response = cachedITunesService.searchForArtists("Queen").block();
            Assertions.assertNotNull(response);
            Assertions.assertEquals(2, response.getResults().size());
            Assertions.assertEquals("Queen", response.getResults().get(0).getArtistName());
            Assertions.assertEquals("King Crimson", response.getResults().get(1).getArtistName());
        }
        Assertions.assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    public void test_SearchForArtists_cachesResponsesByName() throws Exception {
        var artistSearchDTO = ArtistSearchDTO
                .builder()
                .results(List.of(
                        ArtistDTO.builder().artistName("Queen").build()
                ))
                .build();

        var artistSearchDTO2 = ArtistSearchDTO
                .builder()
                .results(List.of(
                        ArtistDTO.builder().artistName("King Crimson").build()
                ))
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(artistSearchDTO))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(artistSearchDTO2))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        for (int i = 0; i < 5; i++) {
            var response = cachedITunesService.searchForArtists("Queen").block();
            Assertions.assertNotNull(response);
            Assertions.assertEquals(1, response.getResults().size());
            Assertions.assertEquals("Queen", response.getResults().get(0).getArtistName());

            response = cachedITunesService.searchForArtists("King Crimson").block();
            Assertions.assertNotNull(response);
            Assertions.assertEquals(1, response.getResults().size());
            Assertions.assertEquals("King Crimson", response.getResults().get(0).getArtistName());
        }
        Assertions.assertEquals(2, mockWebServer.getRequestCount());
    }

}

