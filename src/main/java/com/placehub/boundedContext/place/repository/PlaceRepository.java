package com.placehub.boundedContext.place.repository;

import com.placehub.boundedContext.place.entity.Place;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByPlaceId(Long placeId);

    Page<Place> findAll(Pageable pageable);

    @Query("SELECT p FROM Place p JOIN PlaceLike pl ON p.id = pl.placeId WHERE pl.memberId = :memberId")
    List<Place> findByPlaceLikeList_MemberId(@Param("memberId") Long memberId);

    @Query("SELECT pl FROM Post p JOIN Place pl ON p.place = pl.id JOIN Member m ON p.member = m.id WHERE m.id = :memberId")
    List<Place> findPlacesByMemberId(@Param("memberId") Long memberId);

    List<Place> findPlaceBySpecificDistance(Point point, Long distance);
    Page<Place> findPlaceBySpecificDistance(Pageable pageable, Point point, Long distance);
    Page<Place> findPlaceBySpecificDistanceAndBigId(Pageable pageable, Point point, Long distance, Long bigCategoryId);
    Page<Place> findPlaceBySpecificDistanceAndBigIdAndMidId(Pageable pageable, Point point, Long distance, Long bigCategoryId, Long midCategoryId);
    Page<Place> findPlaceBySpecificDistanceAndBigIdAndMidIdAndSmallId(Pageable pageable, Point point, Long distance, Long bigCategoryId, Long midCategoryId, Long smallCategoryId);
}
