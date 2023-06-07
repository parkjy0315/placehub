package com.placehub.boundedContext.place;

import com.placehub.boundedContext.place.entity.Place;

public class PlaceInfo {
    private Place place;
    private String bigCategoryName;
    private String midCategoryName;
    private String smallCategoryName;

    public PlaceInfo(Place place, String bigCategoryName, String midCategoryName, String smallCategoryName) {
        this.place = place;
        this.bigCategoryName = bigCategoryName;
        this.midCategoryName = midCategoryName;
        this.smallCategoryName = smallCategoryName;
    }

    public Place getPlace() {
        return place;
    }

    public String getBigCategoryName() {
        return bigCategoryName;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }
}