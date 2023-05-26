package com.placehub.boundedContext.post.repository;

import com.placehub.boundedContext.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
