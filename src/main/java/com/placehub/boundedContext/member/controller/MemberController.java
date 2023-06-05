package com.placehub.boundedContext.member.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.placehub.base.appConfig.AppConfig;
import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.base.util.Ut;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.form.Viewer;
import com.placehub.boundedContext.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
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

import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PostService postService;
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
    @GetMapping("/myPage")
    public String showMyPage(Model model) {
        List<Post> postList = this.postService.findByMember(rq.getMember().getId());
        model.addAttribute("postList", postList);
        return "usr/member/myPage";
    }
}
