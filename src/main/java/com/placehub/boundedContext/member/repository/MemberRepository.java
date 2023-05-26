package com.placehub.boundedContext.member.repository;

import com.placehub.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

}
