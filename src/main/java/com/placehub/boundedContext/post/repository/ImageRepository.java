package com.placehub.boundedContext.post.repository;

import com.placehub.boundedContext.post.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Images, Long> {
    List<Images> findImagesByPost(long postId);
}
