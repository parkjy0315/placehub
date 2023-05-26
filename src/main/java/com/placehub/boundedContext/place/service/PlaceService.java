package com.placehub.boundedContext.place.service;

import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public Place create(String placeName, Double xPos, Double yPos, String category) {
        Place place = Place.builder()
                .placeName(placeName)
                .xPos(xPos)
                .yPos(yPos)
                .category(category)
                .build();
        return placeRepository.save(place);
    }

    public Place read(Long id) {
        Optional<Place> place = placeRepository.findById(id);
        return place.orElse(null);
    }

    public Place update(Place place, String placeName, Double xPos, Double yPos, String category) {
        Place updatePlace = place.toBuilder()
                .placeName(placeName)
                .xPos(xPos)
                .yPos(yPos)
                .category(category)
                .build();
        return placeRepository.save(updatePlace);
    }

    public void delete(Place place) {
        placeRepository.delete(place);
    }
}
