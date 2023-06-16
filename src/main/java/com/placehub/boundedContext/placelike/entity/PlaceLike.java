package com.placehub.boundedContext.placelike.entity;

import com.placehub.base.entity.BaseEntity;
import jakarta.persistence.Entity;
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
