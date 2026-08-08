package com.techblog.backend.controller;

import com.techblog.backend.dto.BookmarkDto;
import com.techblog.backend.entity.Bookmark;
import com.techblog.backend.entity.Post;
import com.techblog.backend.entity.User;
import com.techblog.backend.repository.BookmarkRepository;
import com.techblog.backend.repository.PostRepository;
import com.techblog.backend.repository.UserRepository;
import com.techblog.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<BookmarkDto>> getUserBookmarks(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId()).orElseThrow();
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getId());

        List<BookmarkDto> dtos = bookmarks.stream().map(bookmark -> {
            BookmarkDto dto = new BookmarkDto();
            dto.setId(bookmark.getId());
            dto.setPostId(bookmark.getPost().getId());
            dto.setPostTitle(bookmark.getPost().getTitle());
            dto.setCreatedAt(bookmark.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> toggleBookmark(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId) {

        User user = userRepository.findById(principal.getId()).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        // Toggle the bookmark
        if (bookmarkRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            bookmarkRepository.deleteByUserIdAndPostId(user.getId(), postId); // ✅ Uses the new method!
            return ResponseEntity.ok("Bookmark removed");
        } else {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user);
            bookmark.setPost(post);
            bookmarkRepository.save(bookmark);
            return ResponseEntity.ok("Bookmark added");
        }
    }
}