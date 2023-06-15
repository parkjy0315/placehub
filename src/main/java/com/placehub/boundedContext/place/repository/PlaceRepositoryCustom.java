package com.placehub.boundedContext.place.repository;

import com.placehub.boundedContext.place.dto.SearchCriteria;
import com.placehub.boundedContext.place.entity.Place;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaceRepositoryCustom {

    List<Place> findPlaceByDistance(Point point, Long distance);
    Page<Place> findPlaceByDistance(Pageable pageable, Point point, Long distance);
    Page<Place> findPlaceByDistanceAndIds(Pageable pageable, SearchCriteria searchCriteria);
    Page<Place> findPlaceWithPositiveLikeCount(Pageable pageable, Long likeCountCriteria);
}
