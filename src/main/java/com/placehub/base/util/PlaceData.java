package com.placehub.base.util;


import com.placehub.boundedContext.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceData {
    @Autowired
    private final PlaceService placeService;

    public static void savePlace(String categoryCode) {

    }

}
