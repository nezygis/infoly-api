package com.silouks.infoly.favorites.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "favorite_artists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteArtists {
    @Id
    private String userId;
    private Set<Integer> artistIds;
}
