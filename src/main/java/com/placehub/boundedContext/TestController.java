package com.placehub.boundedContext;

import com.placehub.base.util.LocalApi;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @GetMapping("/test")
    public String pageTest1() {
        return "test";
    }

    @GetMapping("/local_api_test")
    @ResponseBody
    public String pageTest2() {
        double xLng = 33.450701; // 경도
        double yLat = 126.570667; // 위도
        String keyWord = "카페"; // 키워드
        int page = 1; // 페이지 수
        int size = 10; // 한 페이지 내 결과 개수
        int radius = 10000; //

        JSONObject result = LocalApi.KeyWord.getAll(xLng, yLat, keyWord, radius, page, size);

        return result.toJSONString();
    }
}
