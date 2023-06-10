package com.placehub.boundedContext.post.repository;

import com.placehub.boundedContext.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<List<Post>> findPostsByPlace(long placeId);
    List<Post> findByMember(Long memberId);
    List<Post> findByPlace(Long placeId);
}
