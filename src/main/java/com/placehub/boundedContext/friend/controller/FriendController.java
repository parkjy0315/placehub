package com.placehub.boundedContext.friend.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.friend.entity.Friend;
import com.placehub.boundedContext.friend.service.FriendService;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.place.PlaceInfo;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.service.PlaceService;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService followService;
    private final MemberService memberService;
    private final PostService postService;
    private final PlaceService placeService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showFriendList(Model model) {
        List<Member> followingList = followService.findFollowing(rq.getMember().getId());
        List<Member> followerList = followService.findFollower(rq.getMember().getId());

        model.addAttribute("followingList",followingList);
        model.addAttribute("followerList",followerList);

        return "usr/member/follow";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{nickname}")
    public ResponseEntity<String> follow(@PathVariable String nickname) {

        RsData<Friend> followRsData = followService.follow(rq.getMember().getId(), nickname);
        if (followRsData.isFail()) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
        }

        return ResponseEntity.ok().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
    }

}
