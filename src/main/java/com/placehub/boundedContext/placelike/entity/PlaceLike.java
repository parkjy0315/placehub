package com.placehub.boundedContext.placelike.entity;

import com.placehub.base.entity.BaseEntity;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.post.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PlaceLike extends BaseEntity {

    private Long memberId;

    private Long placeId;

}
