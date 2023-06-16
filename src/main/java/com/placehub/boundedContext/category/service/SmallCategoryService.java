package com.placehub.boundedContext.category.service;


import com.placehub.boundedContext.category.entity.SmallCategory;
import com.placehub.boundedContext.category.repository.SmallCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmallCategoryService {
    private final SmallCategoryRepository smallCategoryRepository;

    public SmallCategory create(String categoryName, Long midCategoryId) {
        SmallCategory category = SmallCategory.builder()
                .categoryName(categoryName)
                .midCategoryId(midCategoryId)
                .build();
        return smallCategoryRepository.save(category);
    }

    public SmallCategory getSmallCategory(Long id) {
        if (id == null) {
            return new SmallCategory();
        }

        Optional<SmallCategory> category = smallCategoryRepository.findById(id);
        return category.orElse(null);
    }

    public List<SmallCategory> findAll() {
        return smallCategoryRepository.findAll();
    }

    public SmallCategory findByCategoryName(String categoryName) {
        Optional<SmallCategory> category = smallCategoryRepository.findByCategoryName(categoryName);
        return category.orElse(null);
    }

    public SmallCategory update(SmallCategory category, String categoryName, Long midCategoryId) {
        SmallCategory updateCategory = category.toBuilder()
                .categoryName(categoryName)
                .midCategoryId(midCategoryId)
                .build();
        return smallCategoryRepository.save(updateCategory);
    }

    public void delete(SmallCategory category) {
        smallCategoryRepository.delete(category);
    }
}
