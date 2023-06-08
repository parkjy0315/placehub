package com.placehub.boundedContext.follow.repository;

import com.placehub.boundedContext.follow.entity.QFollow;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QFollow follow = QFollow.follow;
    private final QMember member = QMember.member;

    @Override
    public List<Member> findFollowingByFollowerId(Long followerId) {
        return jpaQueryFactory
                .select(member)
                .from(follow, member)
                .where(follow.followingId.eq(member.id)
                        .and(follow.followerId.eq(followerId)))
                .fetch();
    }

    @Override
    public List<Member> findFollowerByFollowingId(Long followingId) {
        return jpaQueryFactory
                .select(member)
                .from(follow, member)
                .where(follow.followerId.eq(member.id)
                        .and(follow.followingId.eq(followingId)))
                .fetch();
    }
}
