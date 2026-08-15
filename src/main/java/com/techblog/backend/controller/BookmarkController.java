package com.techblog.backend.controller;

import com.techblog.backend.entity.Bookmark;
import com.techblog.backend.entity.Post;
import com.techblog.backend.entity.User;
import com.techblog.backend.repository.BookmarkRepository;
import com.techblog.backend.repository.PostRepository;
import com.techblog.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getUserBookmarks(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getId());
        List<Map<String, Object>> result = bookmarks.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("postId", b.getPost().getId());
            map.put("postTitle", b.getPost().getTitle());
            map.put("createdAt", b.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> toggleBookmark(@PathVariable Long postId, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        boolean alreadyBookmarked = bookmarkRepository.existsByUserIdAndPostId(user.getId(), postId);
        if (alreadyBookmarked) {
            Bookmark bookmark = bookmarkRepository.findByUserIdAndPostId(user.getId(), postId)
                    .orElseThrow(() -> new RuntimeException("Bookmark not found"));
            bookmarkRepository.delete(bookmark);
            return ResponseEntity.ok(Map.of("message", "Bookmark removed"));
        } else {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user);
            bookmark.setPost(post);
            bookmarkRepository.save(bookmark);
            return ResponseEntity.ok(Map.of("message", "Bookmark added"));
        }
    }
}