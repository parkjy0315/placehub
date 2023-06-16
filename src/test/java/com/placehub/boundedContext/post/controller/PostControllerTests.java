package com.placehub.boundedContext.post.controller;

import com.placehub.boundedContext.post.service.PostService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
