package com.silouks.infoly.favorites.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration(proxyBeanMethods = false)
public class FavoriteArtistsRouter {

    @Bean
    public RouterFunction<ServerResponse> routeGetFavoriteArtists(FavoriteArtistsHandler favoriteArtistsHandler) {
        return RouterFunctions
                .route(GET("/favorite-artists").and(accept(MediaType.APPLICATION_JSON)), favoriteArtistsHandler::getFavoriteArtists);
    }

    @Bean
    public RouterFunction<ServerResponse> routeAddFavoriteArtist(FavoriteArtistsHandler favoriteArtistsHandler) {
        return RouterFunctions
                .route(POST("/favorite-artists").and(accept(MediaType.APPLICATION_JSON)), favoriteArtistsHandler::addFavoriteArtist);
    }

    @Bean
    public RouterFunction<ServerResponse> routeRemoveFavoriteArtist(FavoriteArtistsHandler favoriteArtistsHandler) {
        return RouterFunctions
                .route(DELETE("/favorite-artists/{id}").and(accept(MediaType.APPLICATION_JSON)), favoriteArtistsHandler::removeFavoriteArtist);
    }
}
