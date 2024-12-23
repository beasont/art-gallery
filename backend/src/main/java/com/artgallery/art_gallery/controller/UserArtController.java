package com.artgallery.art_gallery.controller;

import com.artgallery.art_gallery.model.UserArt;
import com.artgallery.art_gallery.repository.UserArtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.Base64;
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
    // user might be guest => no password => hashedPassword = null
    // if user => store hashed password => can later edit / delete
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
                // store hashed
                String hashed = hashPassword(password.trim());
                userArt.setHashedPassword(hashed);
            } else {
                userArt.setHashedPassword(null);
            }

            userArtRepository.save(userArt);
            return new ResponseEntity<>(userArt, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Upload error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Edit / re-upload
    // user must supply correct password if hashedPassword != null
    @PostMapping(value = "/edit", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> editArt(
         @RequestParam Long artId,
         @RequestParam("file") MultipartFile file,
         @RequestParam(required=false) String username,
         @RequestParam(required=false) String password
    ) {
        try {
            Optional<UserArt> optional = userArtRepository.findById(artId);
            if (optional.isEmpty()) {
                return new ResponseEntity<>("UserArt not found", HttpStatus.BAD_REQUEST);
            }
            UserArt userArt = optional.get();
            if (userArt.getHashedPassword() == null) {
                return new ResponseEntity<>("This artwork was uploaded by a guest. Cannot edit.", HttpStatus.FORBIDDEN);
            }
            if (username == null || password == null) {
                return new ResponseEntity<>("Username + password required to edit.", HttpStatus.BAD_REQUEST);
            }
            // check password
            String hashed = hashPassword(password.trim());
            if (!hashed.equals(userArt.getHashedPassword())) {
                return new ResponseEntity<>("Incorrect login.", HttpStatus.FORBIDDEN);
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return new ResponseEntity<>("Only image/* allowed", HttpStatus.BAD_REQUEST);
            }

            byte[] fileBytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(fileBytes);
            String dataUri = "data:" + contentType + ";base64," + base64;

            userArt.setImageUrl(dataUri);
            userArtRepository.save(userArt);
            return new ResponseEntity<>("Artwork updated!", HttpStatus.OK);

        } catch (NoSuchAlgorithmException ex) {
            return new ResponseEntity<>("Error hashing password", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Edit error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete user art
    @PostMapping("/delete")
    public ResponseEntity<?> deleteArt(
        @RequestParam Long artId,
        @RequestParam(required=false) String username,
        @RequestParam(required=false) String password
    ) {
        try {
            Optional<UserArt> optional = userArtRepository.findById(artId);
            if (optional.isEmpty()) {
                return new ResponseEntity<>("UserArt not found", HttpStatus.BAD_REQUEST);
            }
            UserArt userArt = optional.get();
            // if guest => cannot delete
            if (userArt.getHashedPassword() == null) {
                return new ResponseEntity<>("This artwork was uploaded by a guest. Cannot delete.", HttpStatus.FORBIDDEN);
            }
            if (username == null || password == null) {
                return new ResponseEntity<>("Username + password required to delete", HttpStatus.BAD_REQUEST);
            }
            String hashed = hashPassword(password.trim());
            if (!hashed.equals(userArt.getHashedPassword())) {
                return new ResponseEntity<>("Incorrect login.", HttpStatus.FORBIDDEN);
            }

            userArtRepository.delete(userArt);
            return new ResponseEntity<>("UserArt deleted", HttpStatus.OK);

        } catch (NoSuchAlgorithmException ex) {
            return new ResponseEntity<>("Error hashing password", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(Exception e) {
            return new ResponseEntity<>("Delete error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
