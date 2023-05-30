package com.placehub.boundedContext.member.controller;

import com.placehub.base.rsData.RsData;
import com.placehub.base.util.Ut;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "usr/member/join";
    }

    @AllArgsConstructor
    @Getter
    public static class JoinForm {
        @NotBlank
        @Size(min = 4, max = 20)
        private final String username;
        @NotBlank
        @Size(min = 4, max = 20)
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

        RsData<Member> joinRs = memberService.join(joinForm.getUsername(), joinForm.getPassword(),joinForm.getEmail(),joinForm.getName(),joinForm.getNickname());

        if (joinRs.isFail()) {
            return "common/js";
        }

        String msg = joinRs.getMsg() + "\n로그인 후 이용해주세요.";

        return "redirect:/member/login?msg=" + Ut.url.encode(msg);
    }


    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "usr/member/login";
    }

}
