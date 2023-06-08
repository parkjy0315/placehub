package com.placehub.boundedContext.friend.repository;

import com.placehub.boundedContext.friend.entity.QFriend;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QFriend friend = QFriend.friend;
    private final QMember member = QMember.member;

    @Override
    public List<Member> findFollowingByFollowerId(Long followerId) {
        return jpaQueryFactory
                .select(member)
                .from(friend, member)
                .where(friend.followingId.eq(member.id)
                        .and(friend.followerId.eq(followerId)))
                .fetch();
    }

    @Override
    public List<Member> findFollowerByFollowingId(Long followingId) {
        return jpaQueryFactory
                .select(member)
                .from(friend, member)
                .where(friend.followerId.eq(member.id)
                        .and(friend.followingId.eq(followingId)))
                .fetch();
    }
}
