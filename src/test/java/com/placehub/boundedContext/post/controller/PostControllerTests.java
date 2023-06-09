package com.placehub.boundedContext.post.controller;

import com.placehub.boundedContext.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class PostControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private PostService postService;

//    @Test
//    @DisplayName("게시글 상세 페이지 내 댓글 목록")
//    void t001() throws Exception {
//        // WHEN
//        ResultActions resultActions = mvc.
//                perform(get("/post/view/1"))
//                .andDo(print());
//
//        // THEN
//        resultActions
//                .andExpect(handler().handlerType(PostController.class))
//                .andExpect(handler().methodName("showPost"))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(content().string(containsString("""
//                        테스트 댓글 1
//                         """.stripIndent().trim())));
//
//    }


}
