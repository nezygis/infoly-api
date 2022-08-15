package com.silouks.infoly.artists.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.silouks.infoly.artists.model.ArtistLookupDTO;
import com.silouks.infoly.artists.model.ArtistSearchDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Service
public class CachedITunesService {

    private final Cache<String, ArtistSearchDTO>
            ARTIST_SEARCH_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(6))
            .maximumSize(1_000)
            .build();

    private final Cache<Integer, ArtistLookupDTO>
            ARTIST_LOOKUP_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(6))
            .maximumSize(1_000)
            .build();
    private final ITunesService iTunesService;

    public CachedITunesService(ITunesService iTunesService) {
        this.iTunesService = iTunesService;
    }

    public Mono<ArtistSearchDTO> searchForArtists(String term) {
        Optional<ArtistSearchDTO> artistSearchDTO = Optional.ofNullable(ARTIST_SEARCH_CACHE.getIfPresent(term));
        return artistSearchDTO.map(Mono::just).orElseGet(() -> this.iTunesService.searchForArtists(term)
                .doOnNext(updatedArtistSearchDTO -> ARTIST_SEARCH_CACHE.put(term, updatedArtistSearchDTO)));
    }

    public Mono<ArtistLookupDTO> lookUpArtist(int artistId) {
        Optional<ArtistLookupDTO> artistLookupDTO = Optional.ofNullable(ARTIST_LOOKUP_CACHE.getIfPresent(artistId));
        return artistLookupDTO.map(Mono::just).orElseGet(() -> this.iTunesService.lookUpArtist(artistId)
                .doOnNext(updatedArtistLookupDTO -> ARTIST_LOOKUP_CACHE.put(artistId, updatedArtistLookupDTO)));
    }
}