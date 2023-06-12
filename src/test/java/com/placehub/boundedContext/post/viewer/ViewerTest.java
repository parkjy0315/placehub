package com.placehub.boundedContext.post.viewer;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.post.form.CreatingForm;
import com.placehub.boundedContext.post.form.Viewer;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.post.repository.PostRepository;
import com.placehub.boundedContext.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ViewerTest {
    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("포스팅 뷰어 테스트 성공")
    void postViewerSuccessTest() {
        memberService.create("user", "12345", "홍길동", "gildong@naver.com", "빠더를빠더라부르지못하고");
        CreatingForm creatingForm = new CreatingForm();
        creatingForm.setContent("content");
        creatingForm.setVisitedDate(LocalDate.now());
        creatingForm.setIsOpenToPublic("공개");
        creatingForm.setImages(new ArrayList<>());

        RsData id = postService.createPost(1L, 1L, creatingForm);

        RsData<Viewer> response = postService.showSinglePost((long) id.getData());
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getContent()).isEqualTo("content");
    }

    @Test
    @DisplayName("포스팅 뷰어 테스트 실패")
    void postViewerFailTest() {
        RsData<Viewer> response = postService.showSinglePost(100L);

        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    @DisplayName("포스팅 뷰어 소프트삭제 성공")
    void softDeletePostSuccessTest() {
        CreatingForm creatingForm = new CreatingForm();
        creatingForm.setContent("content");
        creatingForm.setVisitedDate(LocalDate.now());
        creatingForm.setIsOpenToPublic("공개");
        creatingForm.setImages(new ArrayList<>());

        RsData id = postService.createPost(1L, 1L, creatingForm);
        RsData response = postService.deletePost((long) id.getData());
        assertThat(response.isSuccess()).isTrue();
        assertThat(postRepository.findById((long) id.getData()).get().getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("포스팅 뷰어 소프트삭제 실패")
    void softDeletePostFailTest() {
        RsData response = postService.deletePost(0L);

        assertThat(response.isSuccess()).isFalse();
    }
}