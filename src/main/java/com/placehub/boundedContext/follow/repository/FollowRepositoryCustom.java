package com.placehub.boundedContext.follow.repository;

import com.placehub.boundedContext.member.entity.Member;

import java.util.List;

public interface FollowRepositoryCustom {

    List<Member> findFollowingByFollowerId(Long followerId);

    List<Member> findFollowerByFollowingId(Long followingId);
}
