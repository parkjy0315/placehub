package com.placehub.boundedContext.place.factory;

import com.placehub.base.entity.Category;
import com.placehub.boundedContext.place.entity.Place;
import org.locationtech.jts.geom.Point;

public class PlaceFactory {
    public static Place createPlace(Long[] categoryIds,
                                    Long placeId, String placeName,
                                    String phone, String addressName,
                                    Point point) {

        return Place.builder()
                .bigCategoryId(categoryIds[0])
                .midCategoryId(categoryIds[1])
                .smallCategoryId(categoryIds[2])
                .placeId(placeId)
                .placeName(placeName)
                .phone(phone)
                .addressName(addressName)
                .point(point)
                .likeCount(0L)
                .build();
    }

    public static Place createPlace(Long bigCategoryId, Long midCategoryId, Long smallCategoryId,
                                    Long placeId, String placeName,
                                    String phone, String addressName,
                                    Point point) {

        return Place.builder()
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
    }
}
