package com.placehub.boundedContext.post.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class PostEntityTest {
    @Test
    @DisplayName("PostEntity가 생성되는지 확인")
    void createPostEntityTest() {
        long userId = 0L;
        long placeId = 0L;
        String content = "content";
        long like = 0L;
        LocalDateTime createDate = LocalDateTime.now();
        LocalDateTime modifyDate = LocalDateTime.now();
        LocalDateTime deleteDate = LocalDateTime.now();
        boolean openToPublic = true;
        List<Long> commendId = new ArrayList<>();

        Post postEntity1 = Post.builder()
                        .id(0L)
                        .userId(userId)
                        .placeId(placeId)
                        .content(content)
                        .like(like)
                        .createDate(createDate)
                        .modifyDate(modifyDate)
                        .deleteDate(deleteDate)
                        .openToPublic(openToPublic)
//                        .commentId(commendId)
                        .build();
        Post postEntity2 = Post.builder()
                .id(0L)
                .userId(userId)
                .placeId(placeId)
                .content(content)
                .like(like)
                .createDate(createDate)
                .modifyDate(modifyDate)
                .deleteDate(deleteDate)
                .openToPublic(openToPublic)
//                .commentId(commendId)
                .build();

        assertThat(postEntity1).isEqualTo(postEntity2);
    }
}