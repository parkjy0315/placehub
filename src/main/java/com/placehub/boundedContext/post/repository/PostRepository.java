package com.placehub.boundedContext.post.repository;

import com.placehub.boundedContext.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);
    Optional<List<Post>> findPostsByPlace(long placeId);
    List<Post> findByMember(Long memberId);
    Page<Post> findByMember(Long id, Pageable pageable);
    List<Post> findByPlace(Long placeId);
}
