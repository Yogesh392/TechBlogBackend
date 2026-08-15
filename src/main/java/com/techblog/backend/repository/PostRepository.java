package com.techblog.backend.repository;

import com.techblog.backend.entity.Post;
import com.techblog.backend.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    Optional<Post> findBySlug(String slug);

    Page<Post> findAllByStatusOrderByCreatedAtDesc(PostStatus status, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.slug = :tagSlug")
    Page<Post> findByTagSlug(@Param("tagSlug") String tagSlug, Pageable pageable);

    // ✅ H2-compatible search (replaces native PostgreSQL query)
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Post> searchPosts(@Param("q") String q, Pageable pageable);
}