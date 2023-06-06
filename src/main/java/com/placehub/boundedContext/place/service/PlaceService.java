package com.placehub.boundedContext.place.service;

import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public Place create(Long bigCategoryId, Long midCategoryId, Long smallCategoryId,
                        Long placeId, String placeName, String phone, String addressName,
                        Double xPos, Double yPos) {
        Place place = Place.builder()
                .bigCategoryId(bigCategoryId)
                .midCategoryId(midCategoryId)
                .smallCategoryId(smallCategoryId)
                .placeId(placeId)
                .placeName(placeName)
                .phone(phone)
                .addressName(addressName)
                .xPos(xPos)
                .yPos(yPos)
                .build();
        return placeRepository.save(place);
    }

    public Place create(Place place) {
        return placeRepository.save(place);
    }

    public Place read(Long id) {
        Optional<Place> place = placeRepository.findById(id);
        return place.orElse(null);
    }

    public Place findByPlaceId(Long placeId) {
        Optional<Place> place = placeRepository.findByPlaceId(placeId);
        return place.orElse(null);
    }

    public Place update(Place place,
                        Long bigCategoryId, Long midCategoryId, Long smallCategoryId,
                        String placeName, String phone, String addressName,
                        Double xPos, Double yPos) {
        Place updatePlace = place.toBuilder()
                .bigCategoryId(bigCategoryId)
                .midCategoryId(midCategoryId)
                .smallCategoryId(smallCategoryId)
                .placeName(placeName)
                .phone(phone)
                .addressName(addressName)
                .xPos(xPos)
                .yPos(yPos)
                .build();
        return placeRepository.save(updatePlace);
    }

    public void delete(Place place) {
        placeRepository.delete(place);
    }
}
