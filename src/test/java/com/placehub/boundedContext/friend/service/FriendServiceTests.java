package com.placehub.boundedContext.friend.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.friend.entity.Friend;
import com.placehub.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FriendServiceTests {

    @Autowired
    private FriendService followService;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("사용자 팔로우 테스트")
    void t001() throws Exception{
        RsData<Friend> followRsData = followService.follow(1L, "닉네임2");
        Friend follow = followService.findByFollowerIdAndFollowingId(1L, 2L).orElse(null);

        assertThat(followRsData.getResultCode()).isEqualTo("S-1");
        assertThat(follow).isNotNull();
        assertThat(follow.getFollowerId()).isEqualTo(1L);
        assertThat(follow.getFollowingId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("사용자 팔로우 - 없는 사용자")
    void t002() throws Exception{
        RsData<Friend> followRsData = followService.follow(1L, "없는닉네임");
        assertThat(followRsData.getResultCode()).isEqualTo("F-1");
    }

    @Test
    @DisplayName("사용자 팔로우 - 나 자신")
    void t003() throws Exception{
        RsData<Friend> followRsData = followService.follow(1L, "닉네임1");
        assertThat(followRsData.getResultCode()).isEqualTo("F-2");
    }

    @Test
    @DisplayName("사용자 팔로우 - 이미 팔로우")
    void t004() throws Exception{
        followService.follow(1L, "닉네임2");
        RsData<Friend> followRsData = followService.follow(1L, "닉네임2");
        assertThat(followRsData.getResultCode()).isEqualTo("F-3");
    }
}
