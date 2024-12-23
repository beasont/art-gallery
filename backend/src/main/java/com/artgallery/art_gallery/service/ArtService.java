package com.artgallery.art_gallery.service;

import com.artgallery.art_gallery.model.Art;
import com.artgallery.art_gallery.repository.ArtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArtService {
    private final ArtRepository artRepository;

    public List<Art> getAllArt() {
        return artRepository.findAll();
    }

    public Art saveMetArtwork(Map<String, Object> artData) {
        Art art = new Art();
        art.setTitle((String) artData.get("title"));
        art.setArtist((String) artData.get("artistDisplayName"));
        art.setImageUrl((String) artData.get("primaryImage"));
        art.setDescription((String) artData.get("medium"));

        return artRepository.save(art);
    }
}
