package com.placehub.boundedContext.place.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.category.service.BigCategoryService;
import com.placehub.boundedContext.category.service.MidCategoryService;
import com.placehub.boundedContext.category.service.SmallCategoryService;
import com.placehub.boundedContext.place.dto.PlaceInfo;
import com.placehub.boundedContext.place.dto.SearchCriteria;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.factory.PlaceFactory;
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
                        Long placeId, String placeName,
                        String phone, String addressName,
                        Double xPos, Double yPos) {
        Coordinate coord = new Coordinate(xPos, yPos);
        GeometryFactory factory = new GeometryFactory();
        Point point = factory.createPoint(coord);

        Place place = PlaceFactory.createPlace(
                bigCategoryId, midCategoryId, smallCategoryId,
                placeId, placeName,
                phone, addressName,
                point);

        return placeRepository.save(place);
    }

    @Transactional
    public Place create(Long bigCategoryId, Long midCategoryId, Long smallCategoryId,
                        Long placeId, String placeName,
                        String phone, String addressName,
                        Point point) {
        Place place = PlaceFactory.createPlace(
                bigCategoryId, midCategoryId, smallCategoryId,
                placeId, placeName,
                phone, addressName,
                point);

        return placeRepository.save(place);
    }

    @Transactional
    public Place create(Place place) {
        return placeRepository.save(place);
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

    @Transactional
    public Place update(Place place, Place updatedPlace) {
        Place updated = place.toBuilder()
                .bigCategoryId(updatedPlace.getBigCategoryId())
                .midCategoryId(updatedPlace.getMidCategoryId())
                .smallCategoryId(updatedPlace.getSmallCategoryId())
                .placeName(updatedPlace.getPlaceName())
                .phone(updatedPlace.getPhone())
                .addressName(updatedPlace.getAddressName())
                .point(updatedPlace.getPoint())
                .build();
        return placeRepository.save(updated);
    }

    public void delete(Place place) {
        placeRepository.delete(place);
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
    public List<PlaceInfo> getCategoryNamesList(List<Place> placeList) {
        List<PlaceInfo> categoryNamesList = new ArrayList<>();
        placeList.stream().forEach(place -> categoryNamesList.add(getCategoryNames(place)));

        return categoryNamesList;
    }

    public PlaceInfo getCategoryNames(Place place) {
        return new PlaceInfo(place,
                bigCategoryService.getBigCategory(place.getBigCategoryId()).getCategoryName(),
                midCategoryService.getMidCategory(place.getMidCategoryId()).getCategoryName(),
                smallCategoryService.getSmallCategory(place.getSmallCategoryId()).getCategoryName()
        );
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

    public RsData<Place> isValidCoordinate(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            return RsData.of("F-1", "검색 요청이 아닌 초기화면 요청입니다.");
        }
        if (longitude == -1 || latitude == -1) {
            return RsData.of("F-2", "좌표설정이 올바르지 않습니다. 재설정해주세요.");
        } else if (!((-90 <= latitude && latitude <= 90) && (-180 <= longitude && longitude <= 180))) {
            return RsData.of("F-3", "좌표가 유효한 범위를 벗어납니다.");
        }

        return RsData.of("S-1", "정상적인 좌표입니다.");
    }


    public List<Long> makeCategoryList(Long bigCategoryId, Long midCategoryId, Long smallCategoryId) {
        List<Long> categoryIds = new ArrayList<>();

        if (bigCategoryId != null) categoryIds.add(bigCategoryId);
        if (midCategoryId != null) categoryIds.add(midCategoryId);
        if (smallCategoryId != null) categoryIds.add(smallCategoryId);

        return categoryIds;
    }

    public Page<Place> findPlace(Pageable pageable, SearchCriteria searchCriteria) {
        return placeRepository.findPlaceByDistanceAndIds(pageable, searchCriteria);
    }
}
