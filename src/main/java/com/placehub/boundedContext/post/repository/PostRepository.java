package com.placehub.boundedContext.post.repository;

import com.placehub.boundedContext.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByOpenToPublicTrue(Pageable pageable);

    Optional<List<Post>> findPostsByPlace(long placeId);

    Page<Post> findByMember(Long id, Pageable pageable);

    Page<Post> findByMemberAndOpenToPublicIsTrue(Long id, Pageable pageable);

    Page<Post> findByPlace(Long placeId, Pageable pageable);

    Page<Post> findByPlaceAndOpenToPublicIsTrue(Long placeId, Pageable pageable);

    Optional<Post> findFirstByOrderByIdDesc();
}
