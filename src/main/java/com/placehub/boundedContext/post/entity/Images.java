package com.placehub.boundedContext.post.entity;

import com.placehub.base.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@Where(clause = "delete_date is null")
public class Images extends BaseEntity {
    private long post;
    private long image;
}
