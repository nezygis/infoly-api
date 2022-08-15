package com.silouks.infoly.favorites.service;

import com.silouks.infoly.favorites.model.AddFavoriteArtistRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;

@DataMongoTest
public class FavoriteArtistsHandlerTest {
    private FavoriteArtistsHandler favoriteArtistsHandler;

    @Autowired
    private FavoriteArtistsRepository favoriteArtistsRepository;

    @BeforeEach
    public void setup() {
        favoriteArtistsHandler = new FavoriteArtistsHandler(favoriteArtistsRepository);
    }

    @Test
    public void test_favorite_artist_returns_401_without_user_header() throws Exception {
        var requestBuilder = MockServerRequest.builder();
        var response = favoriteArtistsHandler.addFavoriteArtist(requestBuilder.build()).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());

        response = favoriteArtistsHandler.getFavoriteArtists(requestBuilder.build()).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());

        response = favoriteArtistsHandler.removeFavoriteArtist(requestBuilder.build()).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
    }

    @Test
    public void test_favorite_artist_crud() {
        var requestBuilder = MockServerRequest
                .builder()
                .header("user-id", "test");
        var request = requestBuilder.body(Mono.just(new AddFavoriteArtistRequest(4682)));
        var createResponse = favoriteArtistsHandler.addFavoriteArtist(request).block();
        Assertions.assertNotNull(createResponse);
        Assertions.assertEquals(HttpStatus.ACCEPTED, createResponse.statusCode());

        var getResponse = favoriteArtistsHandler.getFavoriteArtists(requestBuilder.build()).block();
        Assertions.assertNotNull(getResponse);
        Assertions.assertEquals("{\"favorites\":[4682]}", ServerResponseExtractor.serverResponseAsString(getResponse));

        request = requestBuilder.body(Mono.just(new AddFavoriteArtistRequest(5205)));
        createResponse = favoriteArtistsHandler.addFavoriteArtist(request).block();
        Assertions.assertNotNull(createResponse);
        Assertions.assertEquals(HttpStatus.ACCEPTED, createResponse.statusCode());

        getResponse = favoriteArtistsHandler.getFavoriteArtists(requestBuilder.build()).block();
        Assertions.assertNotNull(getResponse);
        Assertions.assertEquals("{\"favorites\":[4682,5205]}", ServerResponseExtractor.serverResponseAsString(getResponse));

        var deleteRequest = MockServerRequest
                .builder()
                .header("user-id", "test")
                .pathVariable("id", "4682");
        favoriteArtistsHandler.removeFavoriteArtist(deleteRequest.build()).block();
        getResponse = favoriteArtistsHandler.getFavoriteArtists(requestBuilder.build()).block();
        Assertions.assertNotNull(getResponse);
        Assertions.assertEquals("{\"favorites\":[5205]}", ServerResponseExtractor.serverResponseAsString(getResponse));
    }

}

