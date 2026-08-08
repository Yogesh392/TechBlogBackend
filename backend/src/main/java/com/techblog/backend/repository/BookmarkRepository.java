package com.techblog.backend.repository;

import com.techblog.backend.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
    Optional<Bookmark> findByPostIdAndUserId(Long postId, Long userId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // ⬇️ THIS WAS MISSING ⬇️
    void deleteByUserIdAndPostId(Long userId, Long postId);
}