package com.artgallery.art_gallery.repository;

import com.artgallery.art_gallery.model.UserArt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserArtRepository extends JpaRepository<UserArt, Long> {
    // For filtering by artist name
    List<UserArt> findByArtistIgnoreCase(String artist);

    // Searching by partial title/artist (if needed)
    List<UserArt> findByTitleContainingIgnoreCase(String title);
    List<UserArt> findByArtistContainingIgnoreCase(String artist);
}
