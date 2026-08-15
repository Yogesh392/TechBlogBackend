package com.techblog.backend.controller;

import com.techblog.backend.dto.CreatePostRequest;
import com.techblog.backend.dto.PostDto;
import com.techblog.backend.entity.Post;
import com.techblog.backend.entity.PostStatus;
import com.techblog.backend.entity.User;
import com.techblog.backend.entity.UserProfile;
import com.techblog.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private CommentRepository commentRepository;

    // ✅ PUBLIC: get published posts (paginated)
    @GetMapping("/public/posts")
    public ResponseEntity<Map<String, Object>> getPublicPosts(
            @RequestParam(defaultValue = "0") int page,
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

    // ✅ PUBLIC: get single post by slug
    @GetMapping("/public/posts/{slug}")
    public ResponseEntity<?> getPostBySlug(@PathVariable String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return ResponseEntity.ok(convertToDto(post));
    }

    // Authenticated: get all posts (dashboard)
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

    // Authenticated: update post
    @PutMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @RequestBody Map<String, Object> updates,
                                        Authentication auth) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getAuthor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only edit your own posts"));
        }
        if (updates.containsKey("title")) post.setTitle((String) updates.get("title"));
        if (updates.containsKey("content")) post.setContent((String) updates.get("content"));
        if (updates.containsKey("excerpt")) post.setExcerpt((String) updates.get("excerpt"));
        if (updates.containsKey("coverImageUrl")) post.setCoverImageUrl((String) updates.get("coverImageUrl"));
        postRepository.save(post);
        return ResponseEntity.ok(convertToDto(post));
    }

    // Authenticated: delete post
    @DeleteMapping("/posts/{id}")
    @Transactional
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication auth) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getAuthor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only delete your own posts"));
        }
        bookmarkRepository.deleteByPostId(id);
        likeRepository.deleteByPostId(id);
        commentRepository.deleteByPostId(id);
        postRepository.delete(post);
        return ResponseEntity.ok(Map.of("message", "Post deleted"));
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