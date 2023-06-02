package com.placehub.boundedContext.place.entity;

import com.placehub.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@SuperBuilder(toBuilder = true)
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "delete_date IS NULL")
@SQLDelete(sql = "UPDATE place SET delete_date = CURRENT_TIMESTAMP where id = ?")
public class Place extends BaseEntity {
    private Long bigCategoryId;
    private Long midCategoryId;
    private Long smallCategoryId;
    @Column(unique = true)
    private Long placeId;
    private String placeName;
    private String phone;
    private String addressName;
    private Double xPos;
    private Double yPos;
    private Long likeCount;
}