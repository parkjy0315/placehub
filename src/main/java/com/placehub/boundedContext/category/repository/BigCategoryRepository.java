package com.placehub.boundedContext.category.repository;

import com.placehub.boundedContext.category.entity.BigCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BigCategoryRepository extends JpaRepository<BigCategory, Long> {
    Optional<BigCategory> findByCategoryName(String categoryName);
}
