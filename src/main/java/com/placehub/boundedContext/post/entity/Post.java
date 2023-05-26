package com.placehub.boundedContext.post.entity;

import com.placehub.base.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.Comparator;

@Entity
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@Where(clause = "delete_date is null")
public class Post extends BaseEntity implements Comparable<Post> {
    private long member;
    private long place;
    String content;
    private long likeCount;
    private LocalDateTime visitedDate;
    private boolean openToPublic;

    @Override
    public int compareTo(Post o) {
        return o.getCreateDate().compareTo(getCreateDate());
    }
}