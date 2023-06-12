package com.placehub.boundedContext.place.service;

import com.placehub.boundedContext.category.service.BigCategoryService;
import com.placehub.boundedContext.category.service.MidCategoryService;
import com.placehub.boundedContext.category.service.SmallCategoryService;
import com.placehub.boundedContext.place.PlaceInfo;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final BigCategoryService bigCategoryService;
    private final MidCategoryService midCategoryService;
    private final SmallCategoryService smallCategoryService;

    @Transactional
    public Place create(Long bigCategoryId, Long midCategoryId, Long smallCategoryId,
                        Long placeId, String placeName, String phone, String addressName,
                        Double xPos, Double yPos) {
        Coordinate coord = new Coordinate(xPos, yPos);
        GeometryFactory factory = new GeometryFactory();
        Point point = factory.createPoint(coord);

        Place place = Place.builder()
                .bigCategoryId(bigCategoryId)
                .midCategoryId(midCategoryId)
                .smallCategoryId(smallCategoryId)
                .placeId(placeId)
                .placeName(placeName)
                .phone(phone)
                .addressName(addressName)
                .point(point)
                //.likeCount(0L)
                .build();
        return placeRepository.save(place);
    }

    @Transactional
    public Place create(Long bigCategoryId, Long midCategoryId, Long smallCategoryId,
                        Long placeId, String placeName, String phone, String addressName,
                        Point point) {
        Place place = Place.builder()
                .bigCategoryId(bigCategoryId)
                .midCategoryId(midCategoryId)
                .smallCategoryId(smallCategoryId)
                .placeId(placeId)
                .placeName(placeName)
                .phone(phone)
                .addressName(addressName)
                .point(point)
                .likeCount(0L)
                .build();
        return placeRepository.save(place);
    }

    @Transactional
    public Place create(Place place) {
        return placeRepository.save(place);
    }

    public Place getPlace(Long id) {
        Optional<Place> place = placeRepository.findById(id);
        return place.orElse(null);
    }

    public Place findByPlaceId(Long placeId) {
        Optional<Place> place = placeRepository.findByPlaceId(placeId);
        return place.orElse(null);
    }

    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    public Page<Place> findAll(Pageable pageable) {
        return placeRepository.findAll(pageable);
    }

    @Transactional
    public Place update(Place place,
                        Long bigCategoryId, Long midCategoryId, Long smallCategoryId,
                        String placeName, String phone, String addressName,
                        Point point) {
        Place updatePlace = place.toBuilder()
                .bigCategoryId(bigCategoryId)
                .midCategoryId(midCategoryId)
                .smallCategoryId(smallCategoryId)
                .placeName(placeName)
                .phone(phone)
                .addressName(addressName)
                .point(point)
                .build();
        return placeRepository.save(updatePlace);
    }

    public void delete(Place place) {
        placeRepository.delete(place);
    }

    public List<PlaceInfo> getCategoryNamesList(List<Place> placeList) {
        List<PlaceInfo> categoryNamesList = new ArrayList<>();
        placeList.stream()
                .forEach(place -> {
                    categoryNamesList.add(new PlaceInfo(place,
                                bigCategoryService.getBigCategory(place.getBigCategoryId()).getCategoryName(),
                                midCategoryService.getMidCategory(place.getMidCategoryId()).getCategoryName(),
                                smallCategoryService.getSmallCategory(place.getSmallCategoryId()).getCategoryName()
                            )
                    );
                });
        return categoryNamesList;
    }

    public PlaceInfo getCategoryNames(Place place) {
        return new PlaceInfo(place,
                bigCategoryService.getBigCategory(place.getBigCategoryId()).getCategoryName(),
                midCategoryService.getMidCategory(place.getMidCategoryId()).getCategoryName(),
                smallCategoryService.getSmallCategory(place.getSmallCategoryId()).getCategoryName()
        );
    }

    public List<Place> findPlaceBySpecificDistance(Point point,
                                                   Long distance) {
        return placeRepository.findPlaceBySpecificDistance(point, distance);
    }

    public Page<Place> findPlaceBySpecificDistance(Pageable pageable,
                                                   Point point,
                                                   Long distance) {
        return placeRepository.findPlaceBySpecificDistance(
                pageable,
                point,
                distance);
    }

    public Page<Place> findPlaceBySpecificDistanceAndBigId(Pageable pageable,
                                                           Point point,
                                                           Long distance,
                                                           Long bigCategoryId) {
        return placeRepository.findPlaceBySpecificDistanceAndBigId(
                pageable,
                point,
                distance,
                bigCategoryId);
    }

    public Page<Place> findPlaceBySpecificDistanceAndBigIdAndMidId(Pageable pageable,
                                                                   Point point,
                                                                   Long distance,
                                                                   Long bigCategoryId,
                                                                   Long midCategoryId) {
        return placeRepository.findPlaceBySpecificDistanceAndBigIdAndMidId(
                pageable,
                point,
                distance,
                bigCategoryId,
                midCategoryId);
    }

    public Page<Place> findPlaceBySpecificDistanceAndBigIdAndMidIdAndSmallId(Pageable pageable,
                                                                             Point point,
                                                                             Long distance,
                                                                             Long bigCategoryId,
                                                                             Long midCategoryId,
                                                                             Long smallCategoryId) {
        return placeRepository.findPlaceBySpecificDistanceAndBigIdAndMidIdAndSmallId(
                pageable,
                point,
                distance,
                bigCategoryId,
                midCategoryId,
                smallCategoryId);
    }

    public List<Place> findByPlaceLikeList_MemberId(Long memberId){
        return placeRepository.findByPlaceLikeList_MemberId(memberId);
    }

    @Transactional
    public void whenUpdatePlaceLike(Long placeId, boolean isCreated) {
        Place place = getPlace(placeId);

        if(place.getLikeCount() == null){
            place = place.toBuilder().likeCount(0L).build();
        }

        if(isCreated){
            place = place.toBuilder().likeCount(place.getLikeCount()+1).build();
        }else {
            place = place.toBuilder().likeCount(place.getLikeCount()-1).build();
        }

        placeRepository.save(place);
    }

    public List<Place> findPlacesByMemberId(Long id) {
        return placeRepository.findPlacesByMemberId(id);
    }
}
