package com.placehub.boundedContext.category.repository;

import com.placehub.boundedContext.category.entity.SmallCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmallCategoryRepository extends JpaRepository<SmallCategory, Long> {
}
