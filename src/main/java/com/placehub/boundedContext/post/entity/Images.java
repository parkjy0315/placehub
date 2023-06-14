package com.placehub.boundedContext.post.entity;

import com.placehub.base.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import java.util.Objects;

@Entity
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@Where(clause = "delete_date is null")
public class Images extends BaseEntity {
    private long post;
    private long img;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Images images = (Images) o;
        return post == images.post && img == images.img;
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, img);
    }
}
