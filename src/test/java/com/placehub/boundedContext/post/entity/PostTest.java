package com.placehub.boundedContext.post.entity;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.post.form.CreatingForm;
import com.placehub.boundedContext.post.form.ModifyingForm;
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
import java.util.ArrayList;
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

        CreatingForm creatingForm = new CreatingForm();
        creatingForm.setVisitedDate(LocalDate.now());
        creatingForm.setIsOpenToPublic("공개");
        creatingForm.setImages(new ArrayList<>());
        creatingForm.setContent("content");

        RsData savedId = postService.createPost(1L, 1L, creatingForm);

        assertThat(postRepository.findById((long) savedId.getData()).get().toString()).isEqualTo(expected.toString());

    }

    @Test
    @DisplayName("장소에 따른 게시글 얻기")
    void getPostsByPlaceTest() {
        CreatingForm creatingForm1 = new CreatingForm();
        creatingForm1.setVisitedDate(LocalDate.now());
        creatingForm1.setIsOpenToPublic("공개");
        creatingForm1.setImages(new ArrayList<>());

        CreatingForm creatingForm2 = new CreatingForm();
        creatingForm2.setVisitedDate(LocalDate.now());
        creatingForm2.setIsOpenToPublic("공개");
        creatingForm2.setImages(new ArrayList<>());

        CreatingForm creatingForm3 = new CreatingForm();
        creatingForm3.setVisitedDate(LocalDate.now());
        creatingForm3.setIsOpenToPublic("공개");
        creatingForm3.setImages(new ArrayList<>());
        RsData one = postService.createPost(1L, 1L, creatingForm1);
        RsData two = postService.createPost(1L, 2L, creatingForm2);
        RsData three = postService.createPost(1L, 1L, creatingForm3);

        List<Post> posts = postService.getPostsByPlace(1L);
        assertThat(posts.get(0).toString()).isEqualTo(postRepository.findById((long) three.getData()).get().toString());
        assertThat(posts.get(1).toString()).isEqualTo(postRepository.findById((long) one.getData()).get().toString());
    }

    @Test
    @DisplayName("공개여부 변경 성공시")
    void changePublicStateTest() {
        CreatingForm creatingForm = new CreatingForm();
        creatingForm.setVisitedDate(LocalDate.now());
        creatingForm.setIsOpenToPublic("공개");
        creatingForm.setImages(new ArrayList<>());
        RsData postId = postService.createPost(1L, 1L, creatingForm);

        try {
            long id = postService.changePublicShowing((long) postId.getData(), false);
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
        CreatingForm creatingForm = new CreatingForm();
        creatingForm.setVisitedDate(LocalDate.now());
        creatingForm.setIsOpenToPublic("공개");
        creatingForm.setImages(new ArrayList<>());

        RsData postId = postService.createPost(1L, 1L, creatingForm);

        ModifyingForm modifyingForm = new ModifyingForm();
        modifyingForm.setImages(new ArrayList<>());
        modifyingForm.setVisitedDate(LocalDate.now());

        String modifiedContent = "ReplacedContent";
        modifyingForm.setContent(modifiedContent);

        try {

            long id = postService.modifyContent((long) postId.getData(), modifyingForm);
            assertThat(postRepository.findById(id).get().getContent()).isEqualTo(modifiedContent);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("수정 권한 확인 성공")
    void modifyValidationTest() {
        CreatingForm creatingForm = new CreatingForm();
        creatingForm.setVisitedDate(LocalDate.now());
        creatingForm.setIsOpenToPublic("공개");
        creatingForm.setImages(new ArrayList<>());

        RsData postId = postService.createPost(1L, 1L, creatingForm);
        long userId = postRepository.findById((long) postId.getData()).get().getMember();

        assertThat(postService.validPostOwner(userId, (long) postId.getData()).isSuccess()).isTrue();
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
        CreatingForm creatingForm = new CreatingForm();
        creatingForm.setVisitedDate(LocalDate.now());
        creatingForm.setIsOpenToPublic("공개");
        creatingForm.setImages(new ArrayList<>());
        RsData postId = postService.createPost(1L, 1L, creatingForm);

        assertThat(postService.validPostOwner(0L, (long) postId.getData()).isFail()).isTrue();
        assertThat(postService.validPostOwner(0L, (long) postId.getData()).getMsg()).isEqualTo("이 게시글의 작성자가 아닙니다");
    }
}