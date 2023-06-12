package com.placehub.boundedContext.member.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.placehub.base.appConfig.AppConfig;
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
import com.placehub.boundedContext.post.form.Viewer;
import com.placehub.boundedContext.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PostService postService;
    private final PlaceService placeService;
    private final FriendService friendService;
    private final Rq rq;

    @Autowired
    private ObjectMapper objectMapper;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "usr/member/join";
    }

    @AllArgsConstructor
    @Getter
    public static class JoinForm {
        @NotBlank
        @Size(min = AppConfig.Constraints.USERNAME_MIN_LENGTH, max = AppConfig.Constraints.USERNAME_MAX_LENGTH)
        private final String username;
        @NotBlank
        @Size(min = AppConfig.Constraints.PASSWORD_MIN_LENGTH, max = AppConfig.Constraints.PASSWORD_MAX_LENGTH)
        private final String password;
        @NotBlank
        @Email(message = "유효하지 않은 이메일입니다.")
        private final String email;
        private final String name;
        @NotBlank
        @Size(min = AppConfig.Constraints.NICKNAME_MIN_LENGTH, max = AppConfig.Constraints.NICKNAME_MAX_LENGTH)
        private final String nickname;
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {

        RsData<Member> checkRsData = memberService.checkDuplicateValue(joinForm.getUsername(), joinForm.getEmail(), joinForm.getNickname());

        if (checkRsData.isFail()) {
            return rq.historyBack(checkRsData.getMsg());
        }

        RsData<Member> joinRs = memberService.join(joinForm.getUsername(), joinForm.getPassword(),joinForm.getEmail(),joinForm.getName(),joinForm.getNickname());

        String msg = joinRs.getMsg() + "\n로그인 후 이용해주세요.";

        return rq.redirectWithMsg("/member/login", joinRs);
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "usr/member/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public String showMe(Model model) {
        return "usr/member/me";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/page/me")
    public String showMyPage(Model model) {

        List<Post> postList = this.postService.findByMember(rq.getMember().getId());
        List<Place> placeList = placeService.findByPlaceLikeList_MemberId(rq.getMember().getId());
        List<PlaceInfo> placeInfoList = placeService.getCategoryNamesList(placeList);
        List<Member> followingList = friendService.findFollowing(rq.getMember().getId());
        List<Member> followerList = friendService.findFollower(rq.getMember().getId());

        List<Viewer> postViewerList = new ArrayList<>();
        for (Post post : postList) {
            postViewerList.add(postService.showSinglePost(post.getId()).getData());
        }

        List<Place> visitedPlaces = placeService.findPlacesByMemberId(rq.getMember().getId());
        double xPosAverage = visitedPlaces.stream()
                .mapToDouble(place -> place.getPoint().getX())
                .average()
                .orElse(0);

        double yPosAverage = visitedPlaces.stream()
                .mapToDouble(place -> place.getPoint().getY())
                .average()
                .orElse(0);
        model.addAttribute("xPosAverage", xPosAverage);
        model.addAttribute("yPosAverage", yPosAverage);

        model.addAttribute("postList", postList);
        model.addAttribute("postViewerList", postViewerList);
        model.addAttribute("placeList", placeList);
        model.addAttribute("visitedPlaces", visitedPlaces);
        model.addAttribute("placeInfoList", placeInfoList);
        model.addAttribute("followingList",followingList);
        model.addAttribute("followerList",followerList);

        return "usr/member/myPage";
    }

    // 다른 사용자의 페이지
    @GetMapping("/page/{id}")
    public String showOtherMember(Model model, @PathVariable Long id) {

        Member friend = memberService.findById(id).orElse(null);

        List<Post> postList = postService.findByMember(id);
        List<Place> placeList = placeService.findByPlaceLikeList_MemberId(id);
        List<PlaceInfo> placeInfoList = placeService.getCategoryNamesList(placeList);
        List<Member> followingList = friendService.findFollowing(id);
        List<Member> followerList = friendService.findFollower(id);
        Friend follow = friendService.findByFollowerIdAndFollowingId(rq.getMember().getId(), id).orElse(null);

        model.addAttribute("friend", friend);
        model.addAttribute("follow", follow);
        model.addAttribute("postList", postList);
        model.addAttribute("placeInfoList", placeInfoList);
        model.addAttribute("followingList",followingList);
        model.addAttribute("followerList",followerList);

        return "usr/member/otherMemberPage";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/nickname/{id}")
    public ResponseEntity<String> updateNickname(@PathVariable Long id ,@RequestParam("nickname") String nickname) {
        Member member = rq.getMember();

        RsData updateRsData = memberService.updateNickname(member, id, nickname);

        if(updateRsData.isFail()){
            return ResponseEntity.badRequest().body("{\"message\": \"" + updateRsData.getMsg() + "\"}");
        }

        return ResponseEntity.ok().body("{\"message\": \"닉네임이 %s(으)로 수정되었습니다.\"}".formatted(nickname));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/bio/{id}")
    public ResponseEntity<String> updateBio(@PathVariable Long id, @RequestParam String bio) {

        memberService.updateBio(rq.getMember(), bio);
        return ResponseEntity.ok().body("{\"message\": \"바이오가 등록되었습니다.\"}");
    }

}
