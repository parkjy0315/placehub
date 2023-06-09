package com.placehub.boundedContext.placelike.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.placelike.entity.PlaceLike;
import com.placehub.boundedContext.placelike.service.PlaceLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/place/like")
public class PlaceLikeController {
    private final PlaceLikeService placeLikeService;
    private final Rq rq;

    @PostMapping("/{placeId}")
    public String likePlace(@PathVariable("placeId") Long placeId){

        Member actor = rq.getMember();

        PlaceLike placeLike = placeLikeService.findByPlaceIdAndMemberId(placeId, actor.getId());
        Boolean isPresent = placeLikeService.isPresent(placeLike);

        // 존재하면 삭제
        if(isPresent){
            RsData canDeleteRsData = placeLikeService.canDelete(placeLike, actor);

            if(canDeleteRsData.isFail()) {
                return rq.historyBack(canDeleteRsData);
            }

            RsData deleteRsData = placeLikeService.delete(placeLike);
            return rq.redirectWithMsg("/place/details/%s".formatted(placeId), deleteRsData);
        }

        RsData createRsData = placeLikeService.create(placeId, actor);

        return rq.redirectWithMsg("/place/details/%s".formatted(placeId), createRsData);
    }

}
