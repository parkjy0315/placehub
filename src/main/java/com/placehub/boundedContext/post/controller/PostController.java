package com.placehub.boundedContext.post.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.service.CommentService;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.form.Viewer;
import com.placehub.boundedContext.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final Rq rq;
    private final PostService postService;
    private final CommentService commentService;
    @Data
    class PostForm {
        private String place;
//        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/MM/dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate visitedDate;
        @NotBlank
        private String isOpenToPublic;
        private String content;
    }

    @GetMapping("/create")
    public String create() {
        return "usr/post/create";
    }

//    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid PostForm postForm) {
        long userId = rq.getMember().getId();
        long placeId = postService.convertPlaceToId(postForm.getPlace());
        boolean isOpenToPublic = postForm.getIsOpenToPublic().equals("공개");
        String content = postForm.getContent();
        LocalDate visitedDate = postForm.getVisitedDate();


        postService.createPost(userId, placeId, content, isOpenToPublic, visitedDate);
        return "redirect:/post/list";
    }

    @GetMapping("/list")
    public String list(Model model){
        List<Post> postList = this.postService.findAll();
        model.addAttribute("postList", postList);
        return "usr/post/list";
    }

//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/view/{postId}")
    public String showPost(@PathVariable Long postId, Model model) {
        RsData<Viewer> response = postService.showSinglePost(postId);

        if (response.isFail()) {
            throw new RuntimeException("존재하지 않는 포스팅입니다");
        }

        List<Comment> comments = commentService.findCommentsByPostId(postId);
        model.addAttribute("comments", comments);

        model.addAttribute("postView", response);
        return "usr/post/viewer";
    }

    @PostMapping("softDelete/{postId}")
    public String deletePost(@PathVariable long postId) throws RuntimeException {
        RsData response = postService.deletePost(postId);
        if (response.isFail()) {
            throw new RuntimeException("존재하지 않는 포스팅입니다");
        }

        return rq.redirectWithMsg("/post/list", response);
    }
}
