package com.placehub.boundedContext.placelike.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.placelike.entity.PlaceLike;
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

    @PostMapping("/{placeId}")
    public String likePlace(@PathVariable("placeId") Long placeId){

        PlaceLike placeLike = placeLikeService.findByPlaceIdAndMemberId(placeId, rq.getMember().getId());

        boolean isLiked = placeLikeService.isPlaceLiked(placeId, rq.getMember());

        if(isLiked){
            RsData canDeleteRsData = placeLikeService.canDelete(placeLike, rq.getMember());

            if(canDeleteRsData.isFail()) {
                return rq.historyBack(canDeleteRsData);
            }

            RsData deleteRsData = placeLikeService.delete(placeLike);
            return rq.historyBack(deleteRsData);
        }

        RsData createRsData = placeLikeService.create(placeId, rq.getMember());

        return rq.redirectWithMsg("/place/list", createRsData);
    }

}
