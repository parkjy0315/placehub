package com.placehub.base.util;

import com.placehub.base.entity.Category;
import com.placehub.boundedContext.category.service.BigCategoryService;
import com.placehub.boundedContext.category.service.MidCategoryService;
import com.placehub.boundedContext.category.service.SmallCategoryService;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.factory.PlaceFactory;
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
public class PlaceProcessor {
    private final PlaceService placeService;
    private final BigCategoryService bigCategoryService;
    private final MidCategoryService midCategoryService;
    private final SmallCategoryService smallCategoryService;

    private static double START_X;
    private static double START_Y;
    private static double END_X;
    private static double END_Y;

    @Autowired
    public PlaceProcessor(PlaceService placeService,
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

    public double getXdist() {
        return END_X - START_X;
    }

    public double getYdist() {
        return END_Y - START_Y;
    }

    public void saveAllCategoryData(String categoryCode) {
        double xDist = getXdist();
        double yDist = getYdist();

        double criteria = 0.005;

        IntStream.range(0, (int) (yDist / criteria) + 1)
                .boxed()
                .flatMap(
                        i -> IntStream.range(0, (int) (xDist / criteria) + 1)
                                .mapToObj(j -> new int[]{i, j}))
                .forEach(coords -> processDataAndSave(categoryCode, coords[0], coords[1], criteria));
    }

    public String convertRectString(double[] coords) {
        return String.format("%f,%f,%f,%f", coords[0], coords[1], coords[2], coords[3]);
    }

    public void processDataAndSave(String categoryCode, int i, int j, double criteria) {
        int page = 1; // 페이지 수
        int size = 15; // 한 페이지 내 결과 개수
        double[] coords = getNextCoord(i, j, criteria);
        String rect = convertRectString(coords);

        while (true) {
            JSONObject result = LocalApi.Category.getAllRect(rect, categoryCode, page++, size);
            processJsonAndSavePlacePage(result);
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
    public void processJsonAndSavePlacePage(JSONObject placeData) {
        JSONArray documents = (JSONArray) placeData.get("documents");
        documents.stream()
                .map(element -> mapJsonToPlace((JSONObject) element))
                .forEach(place -> savePlaceData((Place) place));
    }

    public Place mapJsonToPlace(JSONObject element) {
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

        Long[] categoryIds = categoryFilter(categoryName);

        Place place = PlaceFactory.createPlace(categoryIds, placeId, placeName, phone, addressName, point);

        return place;
    }

    public void savePlaceData(Place placeData) {
        Place existingPlace = placeService.findByPlaceId(placeData.getPlaceId());

        if (existingPlace != null) {
            placeService.update(existingPlace, placeData);
        } else {
            placeService.create(placeData);
        }
    }

    public Long[] categoryFilter(String categoryStr) {
        Long[] categoryIds = new Long[3];
        categoryIds[0] = categoryIds[1] = categoryIds[2] = null;
        Category[] categories = new Category[3];
        categories[0] = categories[1] = categories[2] = null;
        String[] split = categoryStr.split(" > ");

        for (int i = 0; i < Math.min(split.length, 3); i++) {
            String temp = split[i].trim();
            switch (i) {
                case 0:
                    categories[i] = bigCategoryService.findByCategoryName(temp);
                    categories[i] = categories[i] == null ? bigCategoryService.create(temp) : categories[i];
                    break;
                case 1:
                    categories[i] = midCategoryService.findByCategoryName(temp);
                    categories[i] = categories[i] == null ? midCategoryService.create(temp, categories[i - 1].getId()) : categories[i];
                    break;
                case 2:
                    categories[i] = smallCategoryService.findByCategoryName(temp);
                    categories[i] = categories[i] == null ? smallCategoryService.create(temp, categories[i - 1].getId()) : categories[i];
                    break;
            }
        }

        for (int i = 0; i < 3; i++) {
            categoryIds[i] = categories[i] == null ? null : categories[i].getId();
        }

        return categoryIds;
    }

    public boolean isLastPage(JSONObject placeData) {
        return (boolean) ((JSONObject) placeData.get("meta")).get("is_end");
    }
}
