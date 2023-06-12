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
        return "usr/KaKaoMapApi/test/mapMarker";
    }
}
