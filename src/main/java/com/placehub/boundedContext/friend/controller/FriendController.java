package com.placehub.boundedContext.friend.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.friend.entity.Friend;
import com.placehub.boundedContext.friend.service.FriendService;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.place.service.PlaceService;
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

    private final FriendService friendService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showFriendList(Model model) {
        List<Member> followingList = friendService.findFollowing(rq.getMember().getId());
        List<Member> followerList = friendService.findFollower(rq.getMember().getId());

        model.addAttribute("followingList",followingList);
        model.addAttribute("followerList",followerList);

        return "usr/member/follow";
    }

    // 닉네임 검색으로 팔로우하는 경우
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/follow/{nickname}")
    public ResponseEntity<String> follow(@PathVariable String nickname) {

        RsData<Friend> followRsData = friendService.follow(rq.getMember().getId(), nickname);
        if (followRsData.isFail()) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
        }

        return ResponseEntity.ok().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
    }

    // 다른 사용자 페이지에서 팔로우 버튼으로 팔로우하는 경우
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/follow/{followingId}")
    public String follow(@PathVariable Long followingId) {

        RsData<Friend> followRsData = friendService.follow(rq.getMember().getId(), followingId);

        if (followRsData.isFail()) {
            return rq.historyBack(followRsData);
        }

        return rq.redirectWithMsg("/member/page/" + followingId ,followRsData);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unfollow/{followingId}")
    public String unfollow(@PathVariable Long followingId) {

        Friend friend = friendService.findByFollowerIdAndFollowingId(rq.getMember().getId(), followingId).orElse(null);

        RsData<Friend> unfollowRsData = friendService.unfollow(friend);

        if(unfollowRsData.isFail()){
            return rq.historyBack(unfollowRsData);
        }

        return rq.redirectWithMsg("/member/page/" + followingId, unfollowRsData);
    }

}
