package com.placehub.boundedContext.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MapController {
    @GetMapping("/map")
    public String showMap() {
        return "usr/KaKaoMapApi/KakaoMapApi";
    }

    @GetMapping("/mapmap")
    public String showMaptest() {
        return "usr/KaKaoMapApi/test/testtestmap";
    }
}
