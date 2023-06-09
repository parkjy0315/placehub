package com.placehub.boundedContext.friend.repository;

import com.placehub.boundedContext.member.entity.Member;

import java.util.List;

public interface FriendRepositoryCustom {

    List<Member> findFollowingByFollowerId(Long followerId);

    List<Member> findFollowerByFollowingId(Long followingId);
}
