package com.artgallery.art_gallery.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserArt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;
    private Integer artYear;

    /**
     * Use @Lob so we can store large strings (Base64 data URIs).
     * This avoids "Value too long for column" errors in H2, etc.
     */
    @Lob
    private String imageUrl;

    // Store hashed password to authenticate edits/deletions
    // Nullable: null indicates a guest-uploaded artwork
    private String hashedPassword; 
}
