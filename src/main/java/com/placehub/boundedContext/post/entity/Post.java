package com.placehub.boundedContext.post.entity;

import com.placehub.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@Where(clause = "delete_date is null")
public class Post extends BaseEntity implements Comparable<Post> {
    private long member;
    private long place;
    @Column(columnDefinition = "TEXT")
    private String content;
    private long likeCount;
    private LocalDate visitedDate;
    private boolean openToPublic;

    @Override
    public int compareTo(Post o) {
        return o.getCreateDate().compareTo(getCreateDate());
    }
}
