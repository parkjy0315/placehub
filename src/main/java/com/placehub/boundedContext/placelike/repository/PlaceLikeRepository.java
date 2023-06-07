package com.placehub.boundedContext.placelike.repository;

import com.placehub.boundedContext.placelike.entity.PlaceLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {
    List<PlaceLike> findByPlaceId(Long placeId);
    boolean existsByPlaceIdAndMemberId(Long placeId, Long memberId);
    PlaceLike findByPlaceIdAndMemberId(Long placeId, Long memberId);
}
