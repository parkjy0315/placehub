package com.placehub.boundedContext.follow.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.follow.entity.Follow;
import com.placehub.boundedContext.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final Rq rq;

    @GetMapping("/list")
    public String showFollow() {
        return "usr/member/follow";
    }

    @PostMapping("/{nickname}")
    public ResponseEntity<String> follow(@PathVariable String nickname) {

        System.out.println("Received nickname: " + nickname);

        RsData<Follow> followRsData = followService.follow(rq.getMember().getId(), nickname);
        if (followRsData.isFail()) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
        }

        //return rq.redirectWithMsg("/follow/list", followRsData);

        return ResponseEntity.ok().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
    }
}
