package com.placehub.boundedContext.place.service;

import com.placehub.boundedContext.category.service.BigCategoryService;
import com.placehub.boundedContext.category.service.MidCategoryService;
import com.placehub.boundedContext.category.service.SmallCategoryService;
import com.placehub.boundedContext.place.dto.PlaceInfo;
import com.placehub.boundedContext.place.entity.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceInfoService {
    private final BigCategoryService bigCategoryService;
    private final MidCategoryService midCategoryService;
    private final SmallCategoryService smallCategoryService;

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
}
