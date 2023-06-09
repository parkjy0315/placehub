package com.placehub.boundedContext.category.entity;

import com.placehub.base.entity.BaseEntity;
import com.placehub.base.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BigCategory extends Category {
    @Column(unique = true)
    private String categoryName;
}