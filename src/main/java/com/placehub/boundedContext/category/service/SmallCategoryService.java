package com.placehub.boundedContext.category.service;


import com.placehub.boundedContext.category.entity.BigCategory;
import com.placehub.boundedContext.category.entity.SmallCategory;
import com.placehub.boundedContext.category.repository.SmallCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmallCategoryService {
    @Autowired
    private final SmallCategoryRepository smallCategoryRepository;

    public SmallCategory create(String categoryName) {
        SmallCategory category = SmallCategory.builder()
                .categoryName(categoryName)
                .build();
        return smallCategoryRepository.save(category);
    }

    public SmallCategory read(Long id) {
        Optional<SmallCategory> category = smallCategoryRepository.findById(id);
        return category.orElse(null);
    }

    public SmallCategory findByCategoryName(String categoryName) {
        Optional<SmallCategory> category = smallCategoryRepository.findByCategoryName(categoryName);
        return category.orElse(null);
    }

    public SmallCategory update(SmallCategory category, String categoryName) {
        SmallCategory updateCategory = category.toBuilder()
                .categoryName(categoryName)
                .build();
        return smallCategoryRepository.save(updateCategory);
    }

    public void delete(SmallCategory category) {
        smallCategoryRepository.delete(category);
    }
}
