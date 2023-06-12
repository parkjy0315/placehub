package com.placehub.boundedContext.post.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.service.CommentService;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.form.CreatingForm;
import com.placehub.boundedContext.post.form.ModifyingForm;
import com.placehub.boundedContext.post.form.Viewer;
import com.placehub.boundedContext.post.service.ImageControlOptions;
import com.placehub.boundedContext.post.service.ImageService;
import com.placehub.boundedContext.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final Rq rq;
    private final PostService postService;
    private final CommentService commentService;
    private final ImageService imageService;
    @GetMapping("/create/{placeId}")
    public String create(Model model, @PathVariable("placeId") long placeId) {
        RsData<String> placeName = postService.displayPlaceDuringCreating(placeId);
        model.addAttribute("placeName", placeName);
        return "usr/post/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{placeId}")
    public String create(@Valid CreatingForm creatingForm, @PathVariable("placeId") long placeId) throws RuntimeException {
        long userId = rq.getMember().getId();

        RsData creatingResult = postService.createPost(userId, placeId, creatingForm);
        if (creatingResult.isFail()) {
            throw new RuntimeException("게시글 등록이 실패하였습니다");
        }

        return rq.redirectWithMsg("/post/view/%s".formatted(creatingResult.getData()), "아카이빙이 등록되었습니다.");
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

        List<Comment> comments = commentService.findNotDeleted(postId);
        List<String> imagePathes = imageService.callImagePathes(postId);
        model.addAttribute("comments", comments);

        model.addAttribute("postView", response);
        model.addAttribute("imageList", imagePathes);
        return "usr/post/viewer";
    }

    @PostMapping("softDelete/{postId}")
    public String deletePost(@PathVariable long postId) throws RuntimeException {
        RsData contentDelete = postService.deletePost(postId);
        if (contentDelete.isFail()) {
            throw new RuntimeException("존재하지 않는 포스팅입니다");
        }

        return rq.redirectWithMsg("/post/list", contentDelete);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("modify/{postId}")
    public String modifyPost(@PathVariable long postId, Model model) {
        long userId = rq.getMember().getId();

        RsData postOwnerValidation = postService.validPostOwner(userId, postId);
        if (postOwnerValidation.isFail()) {
            return postOwnerValidation.getMsg();
        }

        List<String> imagePathes = imageService.callImagePathes(postId);
        RsData<Viewer> response = postService.showSinglePost(postId);
        model.addAttribute("modifyingData", response);
        model.addAttribute("photoList", imagePathes);
        return "/usr/post/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("modify/{postId}")
    public String modifyPost(@Valid ModifyingForm modifyingForm, @PathVariable long postId) throws RuntimeException {
        long userId = rq.getMember().getId();

        RsData postOwnerValidation = postService.validPostOwner(userId, postId);
        if (postOwnerValidation.isFail()) {
            throw new RuntimeException(postOwnerValidation.getMsg());
        }

        postService.modifyContent(postId, modifyingForm);
        return "redirect:/post/list";
    }

}