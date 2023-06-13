package com.placehub.boundedContext.place.dto;

import com.placehub.boundedContext.category.entity.BigCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SearchCriteria {
    private Point point;
    private Long distance;
    private List<Long> categoryIds;

    public SearchCriteria(Point point, Long distance, List<Long> categoryIds) {
        this.point = point;
        this.distance = distance;
        this.categoryIds = categoryIds;
    }

    public boolean isEmpty() {
        return categoryIds.isEmpty();
    }
}
