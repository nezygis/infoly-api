package com.silouks.infoly.artists.service;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class ArtistsHandler {
    private final CachedITunesService iTunesService;

    public ArtistsHandler(CachedITunesService iTunesService) {
        this.iTunesService = iTunesService;
    }

    public Mono<ServerResponse> searchArtists(ServerRequest request) {
        Optional<String> term = request.queryParam("term");
        if (term.isEmpty()) {
            return ServerResponse.badRequest().build();
        }
        return iTunesService.searchForArtists(term.get())
                .flatMap(searchArtistDTO -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(searchArtistDTO)))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> lookUpArtist(ServerRequest request) {
        String amgArtistIdStr;
        int amgArtistId;
        try {
            amgArtistIdStr = request.pathVariable("id");
            amgArtistId = Integer.parseInt(amgArtistIdStr);
        } catch (IllegalArgumentException e) {
            return ServerResponse.badRequest().build();
        }
        return iTunesService.lookUpArtist(amgArtistId)
                .flatMap(artistLookupDTO -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(artistLookupDTO)))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> handleError(Throwable err) {
        if (err instanceof RequestNotPermitted) {
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
