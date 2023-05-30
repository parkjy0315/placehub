package com.placehub.boundedContext.member.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    public Member create(String username, String password, String name, String email, String nickname) {
        Member member = Member.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .nickname(nickname)
                .build();
        return memberRepository.save(member);
    }

    public Member read(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.orElse(null);
    }


    public void delete(Member member) {memberRepository.delete(member);}

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional

    // 일반 회원가입
    public RsData<Member> join(String username, String password, String email, String name, String nickname){
        // "PlaceHub" - 일반 회원가입으로 가입한 회원 확인용
        return join("PlaceHub", username, password, email, name, nickname);
    }

    @Transactional
    public RsData<Member> join(String providerTypeCode, String username, String password, String email, String name, String nickname) {

        if ( findByUsername(username).isPresent() ) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        Member member = Member
                .builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .name(name)
                .nickname(nickname)
                .build();

        memberRepository.save(member);
        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);

    }

    // 소셜 로그인
    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username, String email, String name, String nickname) {
        Optional<Member> opMember = findByUsername(username);

        if (opMember.isPresent()) return RsData.of("S-1", "로그인 되었습니다.", opMember.get());

        return join(providerTypeCode, username, "", email, name, nickname); // 최초 로그인시 실행

    }

}
