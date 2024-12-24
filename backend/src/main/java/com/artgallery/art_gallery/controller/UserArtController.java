package com.artgallery.art_gallery.controller;

import com.artgallery.art_gallery.model.UserArt;
import com.artgallery.art_gallery.repository.UserArtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/user-art")
@CrossOrigin(origins = "http://localhost:4200")
public class UserArtController {

    @Autowired
    private UserArtRepository userArtRepository;

    // Helper to hash password so we never store raw password
    private String hashPassword(String plain) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(plain.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Get all user-submitted artworks
    @GetMapping
    public List<UserArt> getAllUserArt() {
        return userArtRepository.findAll();
    }

    // Create / upload a new user artwork
    @PostMapping(value = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> uploadNewArt(
         @RequestParam("file") MultipartFile file,
         @RequestParam("title") String title,
         @RequestParam("artist") String artist,
         @RequestParam("artYear") Integer artYear,
         @RequestParam(required=false) String username,
         @RequestParam(required=false) String password
    ) {
        try {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            if (contentType == null) {
                return new ResponseEntity<>("Could not determine file content type", HttpStatus.BAD_REQUEST);
            }
            if (!contentType.startsWith("image/")) {
                return new ResponseEntity<>("Only image/* allowed", HttpStatus.BAD_REQUEST);
            }
            if (fileName == null) {
                return new ResponseEntity<>("Invalid file name", HttpStatus.BAD_REQUEST);
            }

            byte[] fileBytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(fileBytes);
            String dataUri = "data:" + contentType + ";base64," + base64;

            UserArt userArt = new UserArt();
            userArt.setTitle(title);
            userArt.setArtist(artist);
            userArt.setArtYear(artYear);
            userArt.setImageUrl(dataUri);

            if (username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
                // Store hashed password
                String hashed = hashPassword(password.trim());
                userArt.setHashedPassword(hashed);
            } else {
                // Guest upload; hashedPassword remains null
                userArt.setHashedPassword(null);
            }

            userArtRepository.save(userArt);
            return new ResponseEntity<>(userArt, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Upload error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Edit / update an existing user artwork
    @PostMapping(value = "/edit", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> editArt(
         @RequestParam Long artId,
         @RequestParam String title,
         @RequestParam String artist,
         @RequestParam Integer artYear,
         @RequestParam(required=false) MultipartFile file,
         @RequestParam String username,
         @RequestParam String password
    ) {
        try {
            Optional<UserArt> optional = userArtRepository.findById(artId);
            if (optional.isEmpty()) {
                return new ResponseEntity<>("UserArt not found", HttpStatus.BAD_REQUEST);
            }
            UserArt userArt = optional.get();

            // Authenticate user
            String hashedInput = hashPassword(password.trim());
            if (userArt.getHashedPassword() == null || !hashedInput.equals(userArt.getHashedPassword())) {
                return new ResponseEntity<>("Incorrect username or password.", HttpStatus.FORBIDDEN);
            }

            // Update fields
            userArt.setTitle(title);
            userArt.setArtist(artist);
            userArt.setArtYear(artYear);

            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return new ResponseEntity<>("Only image/* allowed", HttpStatus.BAD_REQUEST);
                }

                byte[] fileBytes = file.getBytes();
                String base64 = Base64.getEncoder().encodeToString(fileBytes);
                String dataUri = "data:" + contentType + ";base64," + base64;
                userArt.setImageUrl(dataUri);
            }

            userArtRepository.save(userArt);
            return new ResponseEntity<>("Artwork edited successfully!", HttpStatus.OK);

        } catch (NoSuchAlgorithmException ex) {
            return new ResponseEntity<>("Error hashing password", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Edit error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete user art
@PostMapping("/delete")
public ResponseEntity<Map<String, String>> deleteArt(
    @RequestParam Long artId,
    @RequestParam String username,
    @RequestParam String password
) {
    Map<String, String> response = new HashMap<>();
    try {
        Optional<UserArt> optional = userArtRepository.findById(artId);
        if (optional.isEmpty()) {
            response.put("message", "UserArt not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        UserArt userArt = optional.get();

        // Authenticate user
        String hashedInput = hashPassword(password.trim());
        if (userArt.getHashedPassword() == null || !hashedInput.equals(userArt.getHashedPassword())) {
            response.put("message", "Incorrect username or password.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        userArtRepository.delete(userArt);
        response.put("message", "UserArt deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (NoSuchAlgorithmException ex) {
        response.put("message", "Error hashing password.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch(Exception e) {
        response.put("message", "Delete error: " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    
}
