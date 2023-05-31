package com.placehub.boundedContext.placelike.controller;

import com.placehub.base.rq.Rq;
import com.placehub.boundedContext.placelike.service.PlaceLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/placelikes")
public class PlaceLikeController {
    private final PlaceLikeService placeLikeService;
    private final Rq rq;

    @PostMapping("/create/{placeId}")
    public String create(@PathVariable("placeId") Long placeId){

        placeLikeService.create(placeId, rq.getMember());
        return "redirect:/place/list";
    }

}
