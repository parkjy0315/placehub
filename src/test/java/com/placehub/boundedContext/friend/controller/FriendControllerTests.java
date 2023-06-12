package com.placehub.boundedContext.friend.controller;

import com.placehub.boundedContext.friend.entity.Friend;
import com.placehub.boundedContext.friend.service.FriendService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FriendControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FriendService friendService;

    @Test
    @DisplayName("팔로우")
    @WithUserDetails("user1")
    void t001() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/friend/create/닉네임2")
                        .with(csrf()))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(FriendController.class))
                .andExpect(handler().methodName("follow"))
                .andExpect(status().is2xxSuccessful())
                ;

        Friend friend = friendService.findByFollowerIdAndFollowingId(1L, 2L).orElse(null);
        assertThat(friend).isNotNull();
    }

    @Test
    @DisplayName("팔로우 - 자기 자신")
    @WithUserDetails("user1")
    void t002() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/friend/create/닉네임1")
                        .with(csrf()))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(FriendController.class))
                .andExpect(handler().methodName("follow"))
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    @DisplayName("팔로우 - 이미 있는 팔로우")
    @WithUserDetails("user2")
    void t003() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/friend/create/닉네임3")
                        .with(csrf()))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(FriendController.class))
                .andExpect(handler().methodName("follow"))
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    @DisplayName("팔로우 - 존재하지 않는 사용자")
    @WithUserDetails("user1")
    void t004() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/friend/create/없는닉네임")
                        .with(csrf()))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(FriendController.class))
                .andExpect(handler().methodName("follow"))
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    @DisplayName("언팔로우")
    @WithUserDetails("user2")
    void t005() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/friend/delete/3")
                        .with(csrf()))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(FriendController.class))
                .andExpect(handler().methodName("unfollow"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/member/page/3**"))
        ;

        Friend friend = friendService.findByFollowerIdAndFollowingId(2L, 3L).orElse(null);
        assertThat(friend).isNull();
    }

    @Test
    @DisplayName("팔로우 - 아이디")
    @WithUserDetails("user1")
    void t006() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/friend/create/2"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(FriendController.class))
                .andExpect(handler().methodName("follow"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/member/page/2**"))
        ;

        Friend friend = friendService.findByFollowerIdAndFollowingId(1L, 2L).orElse(null);
        assertThat(friend).isNotNull();
    }
}
