package com.silouks.infoly.favorites.service;

import com.silouks.infoly.favorites.model.FavoriteArtists;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteArtistsRepository extends ReactiveCrudRepository<FavoriteArtists, String> {
}
