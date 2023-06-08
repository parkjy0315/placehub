package com.placehub.boundedContext.follow.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.follow.entity.Follow;
import com.placehub.boundedContext.follow.service.FollowService;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/list")
    public String showFollow(Model model) {
        List<Member> followingList = followService.findFollowing(rq.getMember().getId());
        List<Member> followerList = followService.findFollower(rq.getMember().getId());
        model.addAttribute("followingList",followingList);
        model.addAttribute("followerList",followerList);
        return "usr/member/follow";
    }

    @PostMapping("/{nickname}")
    public ResponseEntity<String> follow(@PathVariable String nickname) {

        System.out.println("Received nickname: " + nickname);

        RsData<Follow> followRsData = followService.follow(rq.getMember().getId(), nickname);
        if (followRsData.isFail()) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
        }

        return ResponseEntity.ok().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
    }
}
