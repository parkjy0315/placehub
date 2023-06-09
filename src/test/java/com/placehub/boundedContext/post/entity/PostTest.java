package com.placehub.boundedContext.post.entity;

import com.placehub.boundedContext.post.repository.PostRepository;
import com.placehub.boundedContext.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class PostTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    @Test
    @DisplayName("Post 엔티티 CRUD")
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void postCrudTest() {
        Post expected = Post.builder()
                .member(1L)
                .place(1L)
                .content("content")
                .openToPublic(true)
//                        .deleteDate(now)
                .build();
        // Create
        Post saved = postRepository.save(expected);

        // Read
        assertThat(postRepository.findById(expected.getId()).get().toString()).isEqualTo(saved.toString());

//         Update
        expected = expected.toBuilder()
                .content("ReplacedContent")
                .build();

        postRepository.save(expected);
        assertThat(postRepository.findById(expected.getId()).get().getContent()).isEqualTo("ReplacedContent");

        // Delete
        postRepository.deleteById(1L);
        assertThat(postRepository.findById(1L).isPresent()).isFalse();
    }

    @Test
    @DisplayName("Post 엔티티 Create Service")
    void postCrudServiceTest() {
        LocalDate now = LocalDate.now();
        Post expected = Post.builder()
                .member(1L)
                .place(1L)
                .content("content")
                .visitedDate(now)
                .openToPublic(true)
//                        .deleteDate(now)
                .build();
        long savedId = postService.createPost(1L, 1L, "content", true, now);

        assertThat(postRepository.findById(savedId).get().toString()).isEqualTo(expected.toString());

    }

    @Test
    @DisplayName("장소에 따른 게시글 얻기")
    void getPostsByPlaceTest() {
        LocalDate now = LocalDate.now();
        long one = postService.createPost(1L, 1L, "No.1", true, now);
        long two = postService.createPost(1L, 2L, "No.2", false, now);
        long three = postService.createPost(1L, 1L, "No.3", true, now);

        List<Post> posts = postService.getPostsByPlace(1L);
        assertThat(posts.get(0).toString()).isEqualTo(postRepository.findById(three).get().toString());
        assertThat(posts.get(1).toString()).isEqualTo(postRepository.findById(one).get().toString());
    }

    @Test
    @DisplayName("공개여부 변경 성공시")
    void changePublicStateTest() {
        LocalDate now = LocalDate.now();
        long postId = postService.createPost(1L, 1L, "No.1", true, now);

        try {
            long id = postService.changePublicShowing(postId, false);
            assertThat(postRepository.findById(id).get().isOpenToPublic()).isFalse();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("공개여부 변경시 존재하지 않는 게시글일때")
    void changePublicStateNotExistingPostTest() {
        try {
            postService.changePublicShowing(1L, false);
        } catch (SQLException e) {
            assertThat(e.getMessage()).isEqualTo("존재하지 않는 포스트입니다");
        }
    }

    @Test
    @DisplayName("게시글 내용 수정 성공시")
    void modifyContentTest() throws SQLDataException {
        LocalDate now = LocalDate.now();
        long postId = postService.createPost(1L, 1L, "content", true, now);

        try {
            String modifiedContent = "ReplacedContent";
            long id = postService.modifyContent(postId, modifiedContent, now);
            assertThat(postRepository.findById(id).get().getContent()).isEqualTo(modifiedContent);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("수정 권한 확인 성공")
    void modifyValidationTest() {
        LocalDate now = LocalDate.now();
        long postId = postService.createPost(1L, 1L, "content", true, now);
        long userId = postRepository.findById(postId).get().getMember();

        assertThat(postService.validPostOwner(userId, postId).isSuccess()).isTrue();
    }

    @Test
    @DisplayName("수정 권한 확인 실패 존재하지 않는 글")
    void modifyValidationNotExistTest() {
        assertThat(postService.validPostOwner(0L, 0L).isFail()).isTrue();
        assertThat(postService.validPostOwner(0L, 0L).getMsg()).isEqualTo("존재하지 않는 게시글입니다");
    }

    @Test
    @DisplayName("게시글 내용 수정시 작성자 본인이 아닐때")
    void modifyInvalidAuthorTest() {
        LocalDate now = LocalDate.now();
        long postId = postService.createPost(1L, 1L, "content", true, now);

        assertThat(postService.validPostOwner(0L, postId).isFail()).isTrue();
        assertThat(postService.validPostOwner(0L, postId).getMsg()).isEqualTo("이 게시글의 작성자가 아닙니다");
    }
}