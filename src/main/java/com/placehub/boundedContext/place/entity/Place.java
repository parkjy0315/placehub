package com.placehub.boundedContext.place.entity;

import com.placehub.base.entity.BaseEntity;
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
public class Place extends BaseEntity {
    private String placeName;
    private Double xPos;
    private Double yPos;
    private String category;
    private Long likeCount;
}
