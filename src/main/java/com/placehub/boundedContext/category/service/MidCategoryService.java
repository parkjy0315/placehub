package com.placehub.boundedContext.category.service;


import com.placehub.boundedContext.category.entity.MidCategory;
import com.placehub.boundedContext.category.repository.MidCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MidCategoryService {
    private final MidCategoryRepository midCategoryRepository;

    public MidCategory create(String categoryName, Long bigCategoryId) {
        MidCategory category = MidCategory.builder()
                .categoryName(categoryName)
                .bigCategoryId(bigCategoryId)
                .build();
        return midCategoryRepository.save(category);
    }

    public MidCategory getMidCategory(Long id) {
        Optional<MidCategory> category = midCategoryRepository.findById(id);
        return category.orElse(null);
    }

    public List<MidCategory> findAll() {
        return midCategoryRepository.findAll();
    }

    public MidCategory findByCategoryName(String categoryName) {
        Optional<MidCategory> category = midCategoryRepository.findByCategoryName(categoryName);
        return category.orElse(null);
    }

    public MidCategory update(MidCategory category, String categoryName, Long bigCategoryId) {
        MidCategory updateCategory = category.toBuilder()
                .categoryName(categoryName)
                .bigCategoryId(bigCategoryId)
                .build();
        return midCategoryRepository.save(updateCategory);
    }

    public void delete(MidCategory category) {
        midCategoryRepository.delete(category);
    }
}