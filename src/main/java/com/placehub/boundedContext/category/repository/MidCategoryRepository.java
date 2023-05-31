package com.placehub.boundedContext.category.repository;

import com.placehub.boundedContext.category.entity.MidCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MidCategoryRepository extends JpaRepository<MidCategory, Long> {
    Optional<MidCategory> findByCategoryName(String categoryName);
}
