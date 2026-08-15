package com.techblog.backend.controller;

import com.techblog.backend.dto.PostDto;
import com.techblog.backend.entity.Post;
import com.techblog.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final PostRepository postRepository;

    @GetMapping("/posts")
    public ResponseEntity<Page<PostDto>> searchPosts(
            @RequestParam String q,
            Pageable pageable) {
        return ResponseEntity.ok(
                postRepository.searchPosts(q, pageable)
                        .map(this::convertToDto)
        );
    }

    @GetMapping("/tags")
    public ResponseEntity<Page<PostDto>> searchByTag(
            @RequestParam String tag,
            Pageable pageable) {
        return ResponseEntity.ok(
                postRepository.findByTagSlug(tag, pageable)
                        .map(this::convertToDto)
        );
    }

    private PostDto convertToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setSlug(post.getSlug());
        dto.setContent(post.getContent());
        dto.setExcerpt(post.getExcerpt());
        dto.setCoverImageUrl(post.getCoverImageUrl());
        dto.setAuthorName(post.getAuthor().getUsername());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setViews(post.getViews());
        dto.setLikeCount(post.getLikes().size());
        return dto;
    }
}