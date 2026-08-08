package com.techblog.backend.controller;

import com.techblog.backend.dto.CreatePostRequest;
import com.techblog.backend.dto.PostDto;
import com.techblog.backend.entity.Post;
import com.techblog.backend.entity.PostStatus;
import com.techblog.backend.entity.User;
import com.techblog.backend.entity.UserProfile;
import com.techblog.backend.repository.PostRepository;
import com.techblog.backend.repository.UserProfileRepository;
import com.techblog.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;

    // Public: get published posts
    @GetMapping("/public/posts")
    public ResponseEntity<Map<String, Object>> getPosts(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Page<Post> pagePosts = postRepository.findAllByStatusOrderByCreatedAtDesc(
                PostStatus.PUBLISHED, PageRequest.of(page, size));
        Map<String, Object> response = new HashMap<>();
        response.put("posts", pagePosts.getContent().stream().map(this::convertToDto).collect(Collectors.toList()));
        response.put("currentPage", pagePosts.getNumber());
        response.put("totalPages", pagePosts.getTotalPages());
        response.put("totalElements", pagePosts.getTotalElements());
        return ResponseEntity.ok(response);
    }

    // Public: get single post by slug
    @GetMapping("/public/posts/{slug}")
    public ResponseEntity<?> getPostBySlug(@PathVariable String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return ResponseEntity.ok(convertToDto(post));
    }

    // Authenticated: get all posts (for the dashboard)
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
    }

    // Authenticated: create post
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setSlug(request.getTitle().toLowerCase().replaceAll("\\s+", "-") + "-" + System.currentTimeMillis());
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setCoverImageUrl(request.getCoverImageUrl());
        post.setAuthor(user);
        if (request.getStatus() != null) {
            PostStatus status = PostStatus.valueOf(request.getStatus().toUpperCase());
            post.setStatus(status);
            post.setPublished(status == PostStatus.PUBLISHED);
            if (status == PostStatus.PUBLISHED) {
                post.setPublishedAt(LocalDateTime.now());
            }
        }
        post = postRepository.save(post);
        return ResponseEntity.ok(convertToDto(post));
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
        dto.setAuthorAvatar(getAvatarUrl(post.getAuthor()));
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setViews(post.getViews());
        dto.setLikeCount(post.getLikes().size());
        return dto;
    }

    private String getAvatarUrl(User user) {
        return userProfileRepository.findByUserId(user.getId())
                .map(UserProfile::getAvatarUrl)
                .orElse(null);
    }
}