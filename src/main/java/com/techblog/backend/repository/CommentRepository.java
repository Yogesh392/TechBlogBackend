package com.techblog.backend.repository;

import com.techblog.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);

    // Used by InteractionController.getComments()
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // Used by PostController.deletePost() to clear comments before deleting the post
    void deleteByPostId(Long postId);
}