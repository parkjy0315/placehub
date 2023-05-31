package com.placehub.boundedContext.category.repository;

import com.placehub.boundedContext.category.entity.SmallCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmallCategoryRepository extends JpaRepository<SmallCategory, Long> {
    Optional<SmallCategory> findByCategoryName(String categoryName);
}
