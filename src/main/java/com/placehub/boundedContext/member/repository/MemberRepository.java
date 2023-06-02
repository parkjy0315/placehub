package com.placehub.boundedContext.member.repository;

import com.placehub.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);

}
