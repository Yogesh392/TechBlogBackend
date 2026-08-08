package com.techblog.backend.controller;

import com.techblog.backend.dto.CommentDto;
import com.techblog.backend.dto.CommentRequest;
import com.techblog.backend.entity.Comment;
import com.techblog.backend.entity.Like;
import com.techblog.backend.entity.Post;
import com.techblog.backend.entity.User;
import com.techblog.backend.entity.UserProfile;
import com.techblog.backend.repository.CommentRepository;
import com.techblog.backend.repository.LikeRepository;
import com.techblog.backend.repository.PostRepository;
import com.techblog.backend.repository.UserRepository;
import com.techblog.backend.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InteractionController {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserProfileRepository userProfileRepository;

    // Toggle Like on a Post
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        var existingLike = likeRepository.findByPostIdAndUserId(postId, user.getId());
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            int newCount = likeRepository.countByPostId(postId);
            return ResponseEntity.ok(Map.of("liked", false, "likeCount", newCount));
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            int newCount = likeRepository.countByPostId(postId);
            return ResponseEntity.ok(Map.of("liked", true, "likeCount", newCount));
        }
    }

    // Add Comment to a Post
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setPost(post);
        comment = commentRepository.save(comment);

        return ResponseEntity.ok(convertToDto(comment));
    }

    // Get Comments for a Post
    @GetMapping("/public/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        List<CommentDto> dtos = comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUsername(comment.getUser().getUsername());
        dto.setUserAvatar(userProfileRepository.findByUserId(comment.getUser().getId())
                .map(UserProfile::getAvatarUrl)
                .orElse(null));
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
