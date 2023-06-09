package com.placehub.boundedContext.place.repository;

import com.placehub.boundedContext.place.entity.Place;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByPlaceId(Long placeId);

    @Query("SELECT p FROM Place p JOIN PlaceLike pl ON p.id = pl.placeId WHERE pl.memberId = :memberId")
    List<Place> findByPlaceLikeList_MemberId(@Param("memberId") Long memberId);

    List<Place> findPlaceBySpecificDistance(Point point, Long distance);
}
