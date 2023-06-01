package com.placehub.base.util;


import com.placehub.base.entity.Category;
import com.placehub.boundedContext.category.entity.BigCategory;
import com.placehub.boundedContext.category.entity.MidCategory;
import com.placehub.boundedContext.category.entity.SmallCategory;
import com.placehub.boundedContext.category.service.BigCategoryService;
import com.placehub.boundedContext.category.service.MidCategoryService;
import com.placehub.boundedContext.category.service.SmallCategoryService;
import com.placehub.boundedContext.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PlaceData {
    @Autowired
    private final PlaceService placeService;
    @Autowired
    private final BigCategoryService bigCategoryService;
    @Autowired
    private final MidCategoryService midCategoryService;
    @Autowired
    private final SmallCategoryService smallCategoryService;

    @Transactional
    public void savePlace(JSONObject placeData) {
        JSONArray documents = (JSONArray) placeData.get("documents");
        for (int i = 0; i < documents.size(); i++) {
            JSONObject element = (JSONObject) documents.get(i);

            String categoryName = (String) element.get("category_name");
            String placeName = (String) element.get("place_name");
            String phone = (String) element.get("phone");
            String addressName = (String) element.get("address_name");
            Double xPos = Double.parseDouble((String) element.get("x"));
            Double yPos = Double.parseDouble((String) element.get("y"));

            Category[] categories = categoryFilter(categoryName);


            placeService.create(
                    categories[0].getId(),
                    categories[1].getId(),
                    categories[2].getId(),
                    placeName, phone, addressName,
                    xPos, yPos
            );
        }
    }

    public Category[] categoryFilter(String categoryStr) {
        String[] categorySplit = new String[3];
        String[] temp = categoryStr.split(" > ");
        for (int j = 0; j < Math.min(temp.length, 3); j++) {
            categorySplit[j] = temp[j];
        }

        BigCategory bigCategory = null;
        MidCategory midCategory = null;
        SmallCategory smallCategory = null;
        switch (categorySplit.length) {
            case 3:
                smallCategory = smallCategoryService.findByCategoryName(categorySplit[2]);
            case 2:
                midCategory = midCategoryService.findByCategoryName(categorySplit[1]);
            case 1:
                bigCategory = bigCategoryService.findByCategoryName(categorySplit[0]);
                break;
        }

        if (bigCategory == null) {
            bigCategory = bigCategoryService.create(categorySplit[0]);
        }
        if (midCategory == null) {
            midCategory = midCategoryService.create(categorySplit[1]);
        }
        if (smallCategory == null) {
            smallCategory = smallCategoryService.create(categorySplit[2]);
        }

        return new Category[]{bigCategory, midCategory, smallCategory};
    }

    public boolean isLastPage(JSONObject placeData) {
        /*
        "total_count"
        "is_end"
        "pageable_count"
        "same_name"
         */
        JSONObject meta = (JSONObject) placeData.get("meta");
        boolean isEnd = (boolean) meta.get("is_end");
        return isEnd;
    }


}
