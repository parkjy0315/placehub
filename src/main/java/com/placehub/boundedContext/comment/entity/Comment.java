package com.placehub.boundedContext.comment.entity;

import com.placehub.base.entity.BaseEntity;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.post.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private Post post;
    @ManyToOne
    private Member author;


}
