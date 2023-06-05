package com.placehub.boundedContext.post.repository;

import com.placehub.boundedContext.post.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Images, Long> {
}
