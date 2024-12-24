package com.artgallery.art_gallery.service;

import com.artgallery.art_gallery.model.UserArt;
import com.artgallery.art_gallery.repository.UserArtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserArtService {
    private final UserArtRepository userArtRepository;

    public List<UserArt> getAllArt() {
        return userArtRepository.findAll();
    }

    public UserArt saveUserArtwork(UserArt userArt) {
        return userArtRepository.save(userArt);
    }

    public void deleteUserArt(UserArt userArt) {
        userArtRepository.delete(userArt);
    }

    public UserArt getArtById(Long id) {
        return userArtRepository.findById(id).orElse(null);
    }
}
