package com.silouks.infoly.artists.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ArtistsRouter {

    @Bean
    public RouterFunction<ServerResponse> routeSearchArtists(ArtistsHandler artistsHandler) {
        return RouterFunctions
                .route(GET("/artists").and(accept(MediaType.APPLICATION_JSON)), artistsHandler::searchArtists);
    }

    @Bean
    public RouterFunction<ServerResponse> routeLookupArtist(ArtistsHandler artistsHandler) {
        return RouterFunctions
                .route(GET("/artists/{id}").and(accept(MediaType.APPLICATION_JSON)), artistsHandler::lookUpArtist);
    }
}
