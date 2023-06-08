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

    @GetMapping("/list")
    public String showFriendList(Model model) {
        List<Member> followingList = followService.findFollowing(rq.getMember().getId());
        List<Member> followerList = followService.findFollower(rq.getMember().getId());

        model.addAttribute("followingList",followingList);
        model.addAttribute("followerList",followerList);

        return "usr/member/follow";
    }

    @PostMapping("/{nickname}")
    public ResponseEntity<String> follow(@PathVariable String nickname) {

        RsData<Friend> followRsData = followService.follow(rq.getMember().getId(), nickname);
        if (followRsData.isFail()) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
        }

        return ResponseEntity.ok().body("{\"message\": \"" + followRsData.getMsg() + "\"}");
    }

    @GetMapping("/follow/{id}")
    public String showFriendPage(Model model, @PathVariable Long id) {

        Member friend = memberService.findById(id).orElse(null);

        List<Post> postList = postService.findByMember(id);
        List<Place> placeList = placeService.findByPlaceLikeList_MemberId(id);
        List<PlaceInfo> placeInfoList = placeService.getCategoryNamesList(placeList);
        List<Member> followingList = followService.findFollowing(id);
        List<Member> followerList = followService.findFollower(id);

        model.addAttribute("friend", friend);
        model.addAttribute("postList", postList);
        model.addAttribute("placeInfoList", placeInfoList);
        model.addAttribute("followingList",followingList);
        model.addAttribute("followerList",followerList);

        return "usr/member/friendPage";
    }


}
