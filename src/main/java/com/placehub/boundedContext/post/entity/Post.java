package com.placehub.boundedContext.post.entity;

import com.placehub.base.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@Where(clause = "delete_date is null")
public class Post extends BaseEntity {
    private long userId;
    private long placeId;
    String content;
    private long likeCount;
    private boolean openToPublic;
}
