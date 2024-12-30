package com.artgallery.art_gallery.controller;

import com.artgallery.art_gallery.model.Comment;
import com.artgallery.art_gallery.model.Art;
import com.artgallery.art_gallery.model.UserArt;
import com.artgallery.art_gallery.repository.CommentRepository;
import com.artgallery.art_gallery.repository.ArtRepository;
import com.artgallery.art_gallery.repository.UserArtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private UserArtRepository userArtRepository;

    // Helper to hash password so we never store raw password
    private String hashPassword(String plain) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(plain.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // ============== ADD COMMENT ==============
    @PostMapping
    public ResponseEntity<?> addComment(
            @RequestParam Long artId,
            @RequestParam String text,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password) {
        try {
            Comment comment = new Comment();
            comment.setArtId(artId);
            String usedName = (username == null || username.trim().isEmpty()) ? "Guest" : username.trim();
            comment.setUsername(usedName);
            comment.setText(text);
            comment.setCreatedAt(LocalDateTime.now());

            if (!usedName.equalsIgnoreCase("Guest") && password != null && !password.trim().isEmpty()) {
                comment.setHashedPassword(hashPassword(password.trim()));
            } else {
                comment.setHashedPassword(null);
            }

            // Determine if this artId belongs to main Art or userArt
            boolean isInMain = artRepository.findById(artId).isPresent();
            boolean isInUser = userArtRepository.findById(artId).isPresent();
            if (isInMain) {
                comment.setArtSource("MAIN");
            } else if (isInUser) {
                comment.setArtSource("USER");
            } else {
                // Possibly we do a quick check if artId is in the static list from
                // ArtController
                List<Map<String, String>> mainList = ArtController.getInMemoryArtList();
                boolean foundInMemory = mainList.stream()
                        .anyMatch(m -> m.get("id").equals(String.valueOf(artId)));
                if (foundInMemory) {
                    comment.setArtSource("MAIN");
                } else {
                    comment.setArtSource(null);
                }
            }

            commentRepository.save(comment);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding comment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ============== GET RECENT COMMENTS ==============
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentComments() {
        List<Comment> comments = commentRepository.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        // We'll fetch the in-memory list once
        List<Map<String, String>> mainList = ArtController.getInMemoryArtList();

        for (Comment c : comments) {
            Map<String, Object> out = new HashMap<>();
            out.put("id", c.getId());
            out.put("text", c.getText());
            out.put("username", c.getUsername());
            out.put("createdAt", c.getCreatedAt().toString());
            out.put("isUser", c.getHashedPassword() != null);

            String title = "Unknown Title";
            String artist = "Unknown Artist";

            if ("MAIN".equalsIgnoreCase(c.getArtSource())) {
                // see if in DB
                Art dbArt = artRepository.findById(c.getArtId()).orElse(null);
                if (dbArt != null) {
                    title = dbArt.getTitle();
                    artist = dbArt.getArtist();
                } else {
                    // check in-memory list
                    for (Map<String, String> m : mainList) {
                        if (m.get("id").equals(String.valueOf(c.getArtId()))) {
                            title = m.get("title");
                            artist = m.get("artist");
                            break;
                        }
                    }
                }
            } else if ("USER".equalsIgnoreCase(c.getArtSource())) {
                // check userArt db
                Optional<UserArt> optionalUA = userArtRepository.findById(c.getArtId());
                if (optionalUA.isPresent()) {
                    UserArt ua = optionalUA.get();
                    title = ua.getTitle();
                    artist = ua.getArtist();
                }
            }

            out.put("artTitle", title);
            out.put("artArtist", artist);
            out.put("artId", c.getArtId());
            result.add(out);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ============== DELETE COMMENT ==============
    @PostMapping("/delete")
    public ResponseEntity<?> deleteComment(
            @RequestParam Long commentId,
            @RequestParam String username,
            @RequestParam String password) {
        try {
            Optional<Comment> optional = commentRepository.findById(commentId);
            if (!optional.isPresent()) {
                return new ResponseEntity<>("Comment not found", HttpStatus.BAD_REQUEST);
            }
            Comment c = optional.get();
            if (c.getHashedPassword() == null) {
                return new ResponseEntity<>("Cannot delete a Guest comment", HttpStatus.FORBIDDEN);
            }
            if (username == null || password == null) {
                return new ResponseEntity<>("Username + password required", HttpStatus.BAD_REQUEST);
            }
            if (!c.getUsername().equalsIgnoreCase(username.trim())) {
                return new ResponseEntity<>("Incorrect username for this comment", HttpStatus.FORBIDDEN);
            }
            String hashed = hashPassword(password.trim());
            if (!hashed.equals(c.getHashedPassword())) {
                return new ResponseEntity<>("Incorrect login", HttpStatus.FORBIDDEN);
            }
            commentRepository.delete(c);
            return new ResponseEntity<>("Deleted comment successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting comment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ============== EDIT COMMENT ==============
    @PostMapping("/edit")
    public ResponseEntity<?> editComment(
            @RequestParam Long commentId,
            @RequestParam String newText,
            @RequestParam String username,
            @RequestParam String password) {
        try {
            Optional<Comment> optional = commentRepository.findById(commentId);
            if (!optional.isPresent()) {
                return new ResponseEntity<>("Comment not found", HttpStatus.BAD_REQUEST);
            }
            Comment c = optional.get();
            if (c.getHashedPassword() == null) {
                return new ResponseEntity<>("Cannot edit a Guest comment", HttpStatus.FORBIDDEN);
            }
            if (!c.getUsername().equalsIgnoreCase(username.trim())) {
                return new ResponseEntity<>("Incorrect username for this comment", HttpStatus.FORBIDDEN);
            }
            String hashed = hashPassword(password.trim());
            if (!hashed.equals(c.getHashedPassword())) {
                return new ResponseEntity<>("Incorrect login", HttpStatus.FORBIDDEN);
            }
            c.setText(newText);
            commentRepository.save(c);
            return new ResponseEntity<>("Edited comment successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error editing comment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
