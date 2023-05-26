package com.placehub.boundedContext.post.entity;

import com.placehub.boundedContext.post.repository.PostRepository;
import com.placehub.boundedContext.post.service.PostService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
//@ActiveProfiles("test")
class PostTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;
    private static Post expected;

    @BeforeAll
    private static void makeExpectedPost() {
        expected = Post.builder()
                .userId(1L)
                .placeId(1L)
                .content("content")
                .openToPublic(true)
//                        .deleteDate(now)
                .build();
    }

    @Test
    @DisplayName("Post 엔티티 CRUD")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void postCrudTest() {
        // Create
        postRepository.save(expected);

        // Read
        assertThat(postRepository.findById(1L).get().toString()).isEqualTo(expected.toString());

//         Update
        expected = expected.toBuilder()
                .content("ReplacedContent")
                .build();

        postRepository.save(expected);
        assertThat(postRepository.findById(1L).get().getContent()).isEqualTo("ReplacedContent");

        // Delete
        postRepository.deleteById(1L);
        assertThat(postRepository.findById(1L).isPresent()).isFalse();
    }

    @Test
    @DisplayName("Post 엔티티 Create Service")
    void postCrudServiceTest() {
        long savedId = postService.createPost(1L, 1L, "content", true);

        assertThat(postRepository.findById(savedId).get().toString()).isEqualTo(expected.toString());

    }
}