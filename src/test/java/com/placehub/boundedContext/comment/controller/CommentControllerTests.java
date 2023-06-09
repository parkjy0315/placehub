package com.placehub.boundedContext.comment.controller;


import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.repository.CommentRepository;
import com.placehub.boundedContext.comment.service.CommentService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CommentControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;


    @Test
    @DisplayName("댓글 등록")
    @WithUserDetails("user2")
    public void t001() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/create/1")
                        .with(csrf()) // CSRF 키 생성
                        .param("postId", "1")
                        .param("content", "안녕하세요")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().is3xxRedirection())
        ;

        Comment comment = commentService.findById(2L).orElse(null);
        assertThat(comment).isNotNull();
        assertThat(comment.getPostId()).isEqualTo(1L);
        assertThat(comment.getContent()).isEqualTo("안녕하세요");
        assertThat(comment.getMemberId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("댓글 수정 - 권한 있음")
    @WithUserDetails("user1")
    public void t002() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/update/1")
                        .with(csrf()) // CSRF 키 생성
                        .param("postId", "1")
                        .param("content", "테스트 댓글 1 - 수정")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/post/view/1**"));
        ;

        Comment comment = commentService.findById(1L).orElse(null);
        assertThat(comment).isNotNull();
        assertThat(comment.getPostId()).isEqualTo(1L);
        assertThat(comment.getContent()).isEqualTo("테스트 댓글 1 - 수정");
        assertThat(comment.getMemberId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("댓글 수정 - 권한 없음, 수정 불가")
    @WithUserDetails("user2")
    public void t003() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/update/1")
                        .with(csrf()) // CSRF 키 생성
                        .param("postId", "1")
                        .param("content", "테스트 댓글 1 - 수정")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().is4xxClientError());
        ;

        Comment comment = commentService.findById(1L).orElse(null);
        assertThat(comment).isNotNull();
        assertThat(comment.getPostId()).isEqualTo(1L);
        assertThat(comment.getContent()).isEqualTo("테스트 댓글 1");
        assertThat(comment.getMemberId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("댓글 삭제 - 권한 있음")
    @WithUserDetails("user1")
    public void t004() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/delete/1")
                        .with(csrf()) // CSRF 키 생성
                        .param("postId", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/post/view/1**"));
        ;

        Comment comment = commentService.findById(1L).orElse(null);
        assertThat(comment.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("댓글 삭제 - 권한 없음, 삭제 불가")
    @WithUserDetails("user2")
    public void t005() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/delete/1")
                        .with(csrf()) // CSRF 키 생성
                        .param("postId", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is4xxClientError());
        ;

        Comment comment = commentService.findById(1L).orElse(null);
        assertThat(comment.getDeleteDate()).isNull();
    }

}
