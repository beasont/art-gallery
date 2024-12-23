package com.artgallery.art_gallery.repository;

import com.artgallery.art_gallery.model.Art;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArtRepository extends JpaRepository<Art, Long> {
    List<Art> findByTitleContaining(String title);
}
