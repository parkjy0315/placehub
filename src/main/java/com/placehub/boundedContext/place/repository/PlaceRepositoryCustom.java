package com.placehub.boundedContext.place.repository;

import com.placehub.boundedContext.place.entity.Place;
import org.locationtech.jts.geom.Point;

import java.util.List;

public interface PlaceRepositoryCustom {

    List<Place> findPlaceBySpecificDistance(Point point, Long distance);
}
