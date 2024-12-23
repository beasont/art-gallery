package com.artgallery.art_gallery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Represents a built-in Artwork entity for the main (non-user) gallery.
 */
@Entity
@Data
public class Art {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic fields to align with references in ArtService, ArtRepository
    private String title;
    private String artist;
    private String imageUrl;
    private String description;

    // You can add more fields if necessary
}
