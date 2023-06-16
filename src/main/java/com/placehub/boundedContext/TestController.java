package com.placehub.boundedContext;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/test")
    public String pageTest1() {
        return "usr/KaKaoMapApi/test/mapMarker";
    }
}
