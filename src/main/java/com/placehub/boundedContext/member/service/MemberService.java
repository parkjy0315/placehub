package com.placehub.boundedContext.member.service;

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

    public Member create(String member_id, String password, String username, String email, String nickname) {
        Member member = Member.builder()
                .member_id(member_id)
                .password(password)
                .username(username)
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
    public Member join(String username, String password) {
        Member member = Member
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        return memberRepository.save(member);
    }

}
