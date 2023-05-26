package com.placehub.boundedContext.comment.repository;

import com.placehub.boundedContext.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Override
    Optional<Comment> findById(Long id);
}
