package com.placehub.boundedContext.place.service;

import com.placehub.boundedContext.category.service.BigCategoryService;
import com.placehub.boundedContext.category.service.MidCategoryService;
import com.placehub.boundedContext.category.service.SmallCategoryService;
import com.placehub.boundedContext.place.PlaceInfo;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final BigCategoryService bigCategoryService;
    private final MidCategoryService midCategoryService;
    private final SmallCategoryService smallCategoryService;

    @Transactional
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

    @Transactional
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
}
