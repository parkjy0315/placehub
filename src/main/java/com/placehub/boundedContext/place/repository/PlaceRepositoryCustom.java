package com.placehub.boundedContext.place.repository;

import com.placehub.boundedContext.place.entity.Place;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaceRepositoryCustom {

    List<Place> findPlaceBySpecificDistance(Point point, Long distance);
    Page<Place> findPlaceBySpecificDistance(Pageable pageable, Point point, Long distance);
    Page<Place> findPlaceBySpecificDistanceAndBigId(Pageable pageable, Point point, Long distance, Long bigCategoryId);
    Page<Place> findPlaceBySpecificDistanceAndBigIdAndMidId(Pageable pageable, Point point, Long distance, Long bigCategoryId, Long midCategoryId);
    Page<Place> findPlaceBySpecificDistanceAndBigIdAndMidIdAndSmallId(Pageable pageable, Point point, Long distance, Long bigCategoryId, Long midCategoryId, Long smallCategoryId);
}
