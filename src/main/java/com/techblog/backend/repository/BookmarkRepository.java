package com.techblog.backend.repository;

import com.techblog.backend.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUserId(Long userId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    // Used by PostController.deletePost() to clear bookmarks before deleting the post
    void deleteByPostId(Long postId);
}