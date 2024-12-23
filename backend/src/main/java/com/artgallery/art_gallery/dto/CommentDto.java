package com.artgallery.art_gallery.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long artId;
    private String commentText;
    private String username;
}
