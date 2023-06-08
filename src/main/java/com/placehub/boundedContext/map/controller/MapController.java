package com.placehub.boundedContext.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MapController {
    @GetMapping("/map")
    public String showMap() {
        return "usr/KaKaoMapApi/KakaoMapApi";
    }
}
