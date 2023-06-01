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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
            put("문화시설", "CT1"); // 2522
            put("관광명소", "AT4"); // 1111
            put("음식점", "FD6"); // 135651
            put("카페", "CE7"); // 33501
        }};

        // 총 범위
        double startX = 126.84; // 좌하단 X
        double startY = 37.44; // 좌하단 Y
        double endX = 127.16; // 우상단 X
        double endY = 37.72; // 우상단 Y

        // X 차이 = 0.32 / 0.4 = 8
        // Y 차이 = 0.28 / 0.4 = 7
        double xDist = endX - startX;
        double yDist = endY - startY;
        double criteria = 0.005;

        int page = 1; // 페이지 수
        int size = 15; // 한 페이지 내 결과 개수

//        String totalRect = String.format("%f,%f,%f,%f", startX, startY, endX, endY);
//        JSONObject totalResult = LocalApi.Category.getAllRect(totalRect, categoryCode.get("카페"), page, size);
//        System.out.print("\t" + ((JSONObject) totalResult.get("meta")).get("total_count"));

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("output1.txt"));

            for (int i = 0; i < (int) (yDist / criteria); i++) {
                for (int j = 0; j < (int) (xDist / criteria); j++) {
                    double leftDownX = startX + j * criteria; // 좌하단 X
                    double leftDownY = endY - (i + 1) * criteria; // 좌하단 Y
                    double rightUpX = startX + (j + 1) * criteria; // 우상단 X
                    double rightUpY = endY - i * criteria; // 우상단 Y


                    String rect = String.format("%f,%f,%f,%f", leftDownX, leftDownY, rightUpX, rightUpY);

                    JSONObject result = LocalApi.Category.getAllRect(rect, categoryCode.get("카페"), page, size);
                    // placeData.savePlace(result);

                    bw.write("\t" + ((JSONObject) result.get("meta")).get("total_count"));
                }
                bw.write("\n");
                System.out.println(i + " row complete");
            }
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "Success";
    }
}
