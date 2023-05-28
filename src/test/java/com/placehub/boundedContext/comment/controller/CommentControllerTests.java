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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void t001() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/create")
                        .with(csrf()) // CSRF 키 생성
                        .param("postId", "1")
                        .param("content", "안녕하세요")
                        .param("username", "user1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().is3xxRedirection())
        ;

    }

    @Test
    @DisplayName("댓글 목록")
    public void t002() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/comment/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("getList"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @DisplayName("댓글 수정")
    public void t003() throws Exception {

        Comment comment1 = Comment.builder()
                .postId(1L)
                .content("테스트 내용 1")
                .username("user1")
                .build();

        commentRepository.save(comment1);
        Optional<Comment> comment = commentService.findById(comment1.getId());
        assertTrue(comment.isPresent());

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/update/" + comment1.getId())
                        .with(csrf()) // CSRF 키 생성
                        .param("postId", "1")
                        .param("content", "수정 내용")
                        .param("username", "user1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/comment/list*"));
        ;

        comment = commentService.findById(comment1.getId());
        assertThat(comment.get().getContent()).isEqualTo("수정 내용");

    }

    @Test
    @DisplayName("댓글 삭제")
    public void t004() throws Exception {

        Comment comment2 = Comment.builder()
                .postId(1L)
                .content("테스트 내용 2")
                .username("user2")
                .build();

        commentRepository.save(comment2);
        Optional<Comment> comment = commentService.findById(comment2.getId());
        assertTrue(comment.isPresent());

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/comment/delete/" + comment2.getId())
                        .with(csrf()))
                .andDo(print());
        // THEN
        resultActions
                .andExpect(handler().handlerType(CommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/comment/list*"));


        comment = commentService.findById(comment2.getId());
        assertTrue(comment.isEmpty());
    }

}
