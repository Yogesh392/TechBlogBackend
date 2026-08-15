package com.techblog.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookmarkDto {
    private Long id;
    private Long postId;
    private String postTitle;
    private LocalDateTime createdAt;
}