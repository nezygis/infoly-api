package com.silouks.infoly.artists.service;

import com.silouks.infoly.artists.model.ArtistLookupDTO;
import com.silouks.infoly.artists.model.ArtistSearchDTO;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class ITunesService {

    public static final String ALBUM_WRAPPER_TYPE = "collection";

    private final WebClient ITunesWebClient;

    public ITunesService(WebClient iTunesWebClient) {
        ITunesWebClient = iTunesWebClient;
    }

    @RateLimiter(name="itunesService")
    public Mono<ArtistSearchDTO> searchForArtists(String term) {
        return this.ITunesWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("entity", "allArtist")
                        .queryParam("term", term)
                        .build())
                .retrieve()
                .bodyToMono(ArtistSearchDTO.class);
    }

    @RateLimiter(name="itunesService")
    public Mono<ArtistLookupDTO> lookUpArtist(int amgArtistId) {
        return this.ITunesWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/lookup")
                        .queryParam("entity", "album")
                        .queryParam("limit", 5)
                        .queryParam("amgArtistId", amgArtistId)
                        .build())
                .retrieve()
                .bodyToMono(ArtistLookupDTO.class)
                .flatMap(artistLookupDTO -> {
                    artistLookupDTO.getResults().removeIf(test -> !Objects.equals(test.getWrapperType(), ALBUM_WRAPPER_TYPE));
                    return Mono.just(artistLookupDTO);
                });
    }

}
