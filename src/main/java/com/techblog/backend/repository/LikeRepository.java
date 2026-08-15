package com.techblog.backend.repository;

import com.techblog.backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByPostId(Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    // Used by InteractionController.toggleLike()
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);

    // Used by InteractionController.toggleLike()
    int countByPostId(Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    // Used by PostController.deletePost() to clear likes before deleting the post
    void deleteByPostId(Long postId);
}