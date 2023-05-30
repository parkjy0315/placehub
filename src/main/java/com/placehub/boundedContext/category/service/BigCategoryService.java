package com.placehub.boundedContext.category.service;


import com.placehub.boundedContext.category.entity.BigCategory;
import com.placehub.boundedContext.category.repository.BigCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BigCategoryService {
    @Autowired
    private final BigCategoryRepository bigCategoryRepository;

    public BigCategory create(String categoryName) {
        BigCategory category = BigCategory.builder()
                .categoryName(categoryName)
                .build();
        return bigCategoryRepository.save(category);
    }

    public BigCategory read(Long id) {
        Optional<BigCategory> category = bigCategoryRepository.findById(id);
        return category.orElse(null);
    }

    public BigCategory update(BigCategory category, String categoryName) {
        BigCategory updateCategory = category.toBuilder()
                .categoryName(categoryName)
                .build();
        return bigCategoryRepository.save(updateCategory);
    }

    public void delete(BigCategory category) {
        bigCategoryRepository.delete(category);
    }
}
