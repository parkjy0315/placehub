package com.placehub.boundedContext.category.service;


import com.placehub.boundedContext.category.entity.MidCategory;
import com.placehub.boundedContext.category.repository.MidCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MidCategoryService {
    @Autowired
    private final MidCategoryRepository midCategoryRepository;

    public MidCategory create(String categoryName) {
        MidCategory category = MidCategory.builder()
                .categoryName(categoryName)
                .build();
        return midCategoryRepository.save(category);
    }

    public MidCategory read(Long id) {
        Optional<MidCategory> category = midCategoryRepository.findById(id);
        return category.orElse(null);
    }

    public MidCategory update(MidCategory category, String categoryName) {
        MidCategory updateCategory = category.toBuilder()
                .categoryName(categoryName)
                .build();
        return midCategoryRepository.save(updateCategory);
    }

    public void delete(MidCategory category) {
        midCategoryRepository.delete(category);
    }
}