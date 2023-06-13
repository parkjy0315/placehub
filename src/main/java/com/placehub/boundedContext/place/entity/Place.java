package com.placehub.boundedContext.place.entity;

import com.placehub.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.locationtech.jts.geom.Point;

@SuperBuilder(toBuilder = true)
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "delete_date IS NULL")
@SQLDelete(sql = "UPDATE place SET delete_date = CURRENT_TIMESTAMP where id = ?")
public class Place extends BaseEntity {
    @Index(name = "idx_big")
    private Long bigCategoryId;
    @Index(name = "idx_mid")
    private Long midCategoryId;
    @Index(name = "idx_small")
    private Long smallCategoryId;
    @Column(unique = true)
    private Long placeId;
    private String placeName;
    private String phone;
    private String addressName;
    @Column(columnDefinition = "GEOMETRY", nullable = false)
    @Index(name = "idx_point")
    private Point point;
    private Long likeCount;

    @Override
    public String toString() {
        return "Place{" +
                "bigCategoryId=" + bigCategoryId +
                ", midCategoryId=" + midCategoryId +
                ", smallCategoryId=" + smallCategoryId +
                ", placeId=" + placeId +
                ", placeName='" + placeName + '\'' +
                ", phone='" + phone + '\'' +
                ", addressName='" + addressName + '\'' +
                ", point=" + point +
                ", likeCount=" + likeCount +
                '}';
    }
}