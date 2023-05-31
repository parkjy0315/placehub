package com.placehub.boundedContext.place.controller;

import com.placehub.base.util.LocalApi;
import com.placehub.base.util.PlaceData;
import com.placehub.boundedContext.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    @Autowired
    private final PlaceService placeService;
    @Autowired
    private final PlaceData placeData;

    @GetMapping("/keyWordTest/{keyWord}/{radius}")
    @ResponseBody
    public String getKeyWord(@PathVariable("keyWord") String keyWord, @PathVariable("radius") Integer radius) {
        double xLng = 37.532878; // 경도
        double yLat = 126.981969; // 위도
        int page = 1; // 페이지 수
        int size = 15; // 한 페이지 내 결과 개수

        JSONObject result = LocalApi.KeyWord.getAll(xLng, yLat, keyWord, radius, page, size);

        return result.toJSONString();
    }

    @GetMapping("/categoryTest/{category}/{radius}")
    @ResponseBody
    public String getCategory(@PathVariable("category") String category, @PathVariable("radius") Integer radius) {
        double xLng = 37.532878; // 경도
        double yLat = 126.981969; // 위도
        int page = 1; // 페이지 수
        int size = 15; // 한 페이지 내 결과 개수

        JSONObject result = LocalApi.Category.getAll(xLng, yLat, category, radius, page, size);

        return result.toJSONString();
    }

    @GetMapping("/data-test")
    @ResponseBody
    public String saveData() {
        Map<String, String> categoryCode = new HashMap<>() {{
            put("문화시설", "CT1");
            put("관광명소", "AT4");
            put("음식점", "FD6");
            put("카페", "CE7");
        }};

        double xLng = 37.532878; // 경도
        double yLat = 126.981969; // 위도
        int page = 1; // 페이지 수
        int size = 15; // 한 페이지 내 결과 개수

        JSONObject result = LocalApi.Category.getAll(xLng, yLat, categoryCode.get("관광명소"), 20000, page, size);
        placeData.savePlace(result);
        return result.toJSONString();
    }


}
