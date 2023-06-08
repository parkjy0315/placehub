package com.placehub.base.security;

import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;

    // 소셜 로그인 성공 시 실행
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String oauthId = oAuth2User.getName();

        String providerTypeCode = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        String email = "";
        String name = "";

        if (providerTypeCode.equals("KAKAO")) {

            Map<String, Object> userInfo = oAuth2User.getAttribute("kakao_account");
            email = extractValue(userInfo, "email");

            Map<String, Object> profile = extractValue(userInfo, "profile");

            // 사용자 이름 - 개발 단계에선 권한 없음으로 불러오기 불가 - 임시로 닉네임과 동일하게 설정
            // TODO : 카카오 비즈앱 전환 후 이름 가져오기 권한 받고 수정해야함
            // name = extractValue(userInfo, "name");
            name = extractValue(profile, "nickname");
        }

        if(providerTypeCode.equals("NAVER")){

            Map<String, String> userInfo = (Map<String, String>) oAuth2User.getAttributes().get("response");
            oauthId = userInfo.get("id");
            name = userInfo.get("name");
            email = userInfo.get("email");

        }

        if (providerTypeCode.equals("GOOGLE")) {
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
        }

        String username = providerTypeCode + "__%s".formatted(oauthId);
        String nickname = providerTypeCode + "__%s".formatted(oauthId); // 소셜로그인 시 닉네임은 username과 동일하게

        if(nickname.length() > 20) nickname = nickname.substring(0,20);

        Member member = memberService.whenSocialLogin(providerTypeCode, username, email, name, nickname).getData();

        return new CustomOAuth2User(member.getUsername(), member.getPassword(), member.getGrantedAuthorities());
    }

    private <T> T extractValue(Map<String, Object> map, String key) {
        if (map != null && map.containsKey(key)) {
            return (T) map.get(key);
        }
        return null;
    }

}

class CustomOAuth2User extends User implements OAuth2User {

    public CustomOAuth2User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return getUsername();
    }


}