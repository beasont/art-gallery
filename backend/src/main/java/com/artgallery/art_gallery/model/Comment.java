package com.artgallery.art_gallery.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long artId;         // ID of either Art or UserArt
    private String username;    // "Guest" or a chosen user
    private String text;
    private LocalDateTime createdAt;

    // If "User" selected, store hashed password. If "Guest", this can be null or empty.
    private String hashedPassword;

    // We’ll store which "type" of art: "main" or "user" or null
    // Alternatively, we try to see if "artId" is found in Art or in UserArt.
    // We'll keep a field for clarity:
    private String artSource; // "MAIN" or "USER"
}