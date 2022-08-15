package com.silouks.infoly.favorites.service;

import com.silouks.infoly.favorites.model.AddFavoriteArtistRequest;
import com.silouks.infoly.favorites.model.FavoriteArtists;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class FavoriteArtistsHandler {
    private static final String USER_ID_HEADER = "user-id";
    private final FavoriteArtistsRepository favoriteArtistsRepository;

    public FavoriteArtistsHandler(FavoriteArtistsRepository favoriteArtistsRepository) {
        this.favoriteArtistsRepository = favoriteArtistsRepository;
    }

    public Mono<ServerResponse> getFavoriteArtists(ServerRequest request) {
        String userId = request.headers().firstHeader(USER_ID_HEADER);
        if (userId == null) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(favoriteArtistsRepository.findById(userId)
                        .map(favoriteArtists -> Collections.singletonMap("favorites", favoriteArtists.getArtistIds())), Map.class);
    }

    public Mono<ServerResponse> addFavoriteArtist(ServerRequest request) {
        String userId = request.headers().firstHeader(USER_ID_HEADER);
        if (userId == null) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }
        Mono<AddFavoriteArtistRequest> addArtistRequest = request.bodyToMono(AddFavoriteArtistRequest.class);
        Mono<FavoriteArtists> updatedFavoriteArtists = addArtistRequest.flatMap(updateRequest -> {
            if (updateRequest.getId() == null) {
                throw new ServerWebInputException("id is required");
            }
            return favoriteArtistsRepository.findById(userId).flatMap(artists -> {
                if (artists.getArtistIds() == null) {
                    artists.setArtistIds(Set.of(updateRequest.getId()));
                } else {
                    artists.getArtistIds().add(updateRequest.getId());
                }
                return favoriteArtistsRepository.save(artists);
            }).switchIfEmpty(favoriteArtistsRepository.save(new FavoriteArtists(userId, Set.of(updateRequest.getId()))));
        });
        return updatedFavoriteArtists
                .flatMap(favoriteArtists -> ServerResponse.accepted().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(favoriteArtists)));
    }

    public Mono<ServerResponse> removeFavoriteArtist(ServerRequest request) {
        String userId = request.headers().firstHeader(USER_ID_HEADER);
        if (userId == null) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }
        String artistIdStr = request.pathVariable("id");
        int artistId;
        try {
            artistId = Integer.parseInt(artistIdStr);
        }
        catch (NumberFormatException e) {
            return ServerResponse.badRequest().build();
        }
        Mono<FavoriteArtists> updatedFavoriteArtists = favoriteArtistsRepository.findById(userId).flatMap(artists -> {
            if (artists.getArtistIds() != null) {
                artists.getArtistIds().remove(artistId);
            }
            return favoriteArtistsRepository.save(artists);
        });
        return updatedFavoriteArtists.flatMap(favoriteArtists -> ServerResponse.accepted().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(favoriteArtists)));
    }
}
