package com.placehub.boundedContext.member.service;

import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private MemberRepository memberRepository;
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

}
