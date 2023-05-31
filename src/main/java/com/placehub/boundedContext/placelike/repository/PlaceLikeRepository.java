package com.placehub.boundedContext.placelike.repository;

import com.placehub.boundedContext.placelike.entity.PlaceLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {
    boolean existsByPlacdIdAndMemberId(Long placeId, Long memberId);
    PlaceLike findByPlacdIdAndMemberId(Long placeId, Long memberId);
}
