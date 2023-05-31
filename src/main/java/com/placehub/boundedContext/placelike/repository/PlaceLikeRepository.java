package com.placehub.boundedContext.placelike.repository;

import com.placehub.boundedContext.placelike.entity.PlaceLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {
    boolean existsByPlaceIdAndMemberId(Long placeId, Long memberId);
    PlaceLike findByPlaceIdAndMemberId(Long placeId, Long memberId);
}
