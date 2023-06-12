package com.placehub.base.util;

import com.placehub.base.entity.Category;
import com.placehub.boundedContext.category.entity.BigCategory;
import com.placehub.boundedContext.category.entity.MidCategory;
import com.placehub.boundedContext.category.entity.SmallCategory;
import com.placehub.boundedContext.category.service.BigCategoryService;
import com.placehub.boundedContext.category.service.MidCategoryService;
import com.placehub.boundedContext.category.service.SmallCategoryService;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class PlaceData {
    private final PlaceService placeService;
    private final BigCategoryService bigCategoryService;
    private final MidCategoryService midCategoryService;
    private final SmallCategoryService smallCategoryService;

    private static double START_X;
    private static double START_Y;
    private static double END_X;
    private static double END_Y;

    @Autowired
    public PlaceData(PlaceService placeService,
                     BigCategoryService bigCategoryService,
                     MidCategoryService midCategoryService,
                     SmallCategoryService smallCategoryService,
                     Environment environment) {
        this.placeService = placeService;
        this.bigCategoryService = bigCategoryService;
        this.midCategoryService = midCategoryService;
        this.smallCategoryService = smallCategoryService;

        START_X = Double.parseDouble(environment.getProperty("custom.api.coord.start-x"));
        START_Y = Double.parseDouble(environment.getProperty("custom.api.coord.start-y"));
        END_X = Double.parseDouble(environment.getProperty("custom.api.coord.end-x"));
        END_Y = Double.parseDouble(environment.getProperty("custom.api.coord.end-y"));
    }

    public void saveAllCategoryData(String categoryCode) {
        double xDist = END_X - START_X;
        double yDist = END_Y - START_Y;

        double criteria = 0.005;

        IntStream.range(0, (int) (yDist / criteria) + 1)
                .boxed()
                .flatMap(i ->
                        IntStream.range(0, (int) (xDist / criteria) + 1)
                                .mapToObj(j -> new int[]{i, j}))
                .forEach(coords -> fetchPlaceInfo(categoryCode, coords[0], coords[1], criteria));
    }

    public String convertRectString(double [] coords) {
        return String.format("%f,%f,%f,%f", coords[0], coords[1], coords[2], coords[3]);
    }

    public void fetchPlaceInfo(String categoryCode, int i, int j, double criteria) {
        int page = 1; // 페이지 수
        int size = 15; // 한 페이지 내 결과 개수
        double[] coords = getNextCoord(i, j, criteria);
        String rect = convertRectString(coords);

        while (true) {
            JSONObject result = LocalApi.Category.getAllRect(rect, categoryCode, page++, size);
            savePlace(result);
            System.out.println("total_count = " + ((JSONObject) result.get("meta")).get("total_count"));
            System.out.printf("%d %d page = %d\n", i, j, page);
            if (isLastPage(result)) {
                break;
            }
        }
    }

    public double[] getNextCoord(int i, int j, double criteria) {
        double leftDownX = START_X + j * criteria; // 좌하단 X
        double leftDownY = END_Y - (i + 1) * criteria; // 좌하단 Y
        double rightUpX = START_X + (j + 1) * criteria; // 우상단 X
        double rightUpY = END_Y - i * criteria; // 우상단 Y

        return new double[]{leftDownX, leftDownY, rightUpX, rightUpY};
    }

    @Transactional
    public void savePlace(JSONObject placeData) {
        JSONArray documents = (JSONArray) placeData.get("documents");
        documents.stream()
                .forEach(element -> saveData((JSONObject) element));
    }

    public Place convertPlace(JSONObject element) {
        String categoryName = (String) element.get("category_name");
        String placeName = (String) element.get("place_name");
        String phone = (String) element.get("phone");
        String addressName = (String) element.get("address_name");
        Long placeId = Long.parseLong((String) element.get("id"));
        Double xPos = Double.parseDouble((String) element.get("x"));
        Double yPos = Double.parseDouble((String) element.get("y"));
        Coordinate coord = new Coordinate(xPos, yPos);
        GeometryFactory factory = new GeometryFactory();
        Point point = factory.createPoint(coord);

        Category[] categories = categoryFilter(categoryName);

        Place place = Place.builder()
                .bigCategoryId(categories[0].getId())
                .midCategoryId(categories[1].getId())
                .smallCategoryId(categories[2].getId())
                .placeId(placeId)
                .placeName(placeName)
                .phone(phone)
                .addressName(addressName)
                .point(point)
                //.likeCount(0L)
                .build();

        return place;
    }
    public void saveData(JSONObject element) {
        Long placeId = Long.parseLong((String) element.get("id"));

        if (placeService.findByPlaceId(placeId) != null) {
            return;
        }

        Place place = convertPlace(element);

        placeService.create(place);
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
            midCategory = midCategoryService.create(categorySplit[1], bigCategory.getId());
        }
        if (smallCategory == null) {
            smallCategory = smallCategoryService.create(categorySplit[2], midCategory.getId());
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
        return (boolean) meta.get("is_end");
    }
}
