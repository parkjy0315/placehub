package com.placehub.boundedContext.place.repository;

import com.placehub.boundedContext.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByPlaceId(Long placeId);
}
