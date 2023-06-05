package com.placehub.boundedContext.post.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.service.CommentService;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.form.CreatingForm;
import com.placehub.boundedContext.post.form.ModifyingForm;
import com.placehub.boundedContext.post.form.Viewer;
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
    @GetMapping("/create")
    public String create() {
        return "usr/post/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid CreatingForm creatingForm) {
        long userId = rq.getMember().getId();

        long placeId = postService.convertPlaceToId(creatingForm.getPlace());
        boolean isOpenToPublic = creatingForm.getIsOpenToPublic().equals("공개");
        String content = creatingForm.getContent();
        LocalDate visitedDate = creatingForm.getVisitedDate();
        List<MultipartFile> images = creatingForm.getImages();
        System.out.println(images.get(0)  + "IMAAAAAA");
        System.out.println(images.size() + "SSSSSSSSSSSSSSSIZE");


       long postId = postService.createPost(userId, placeId, content, isOpenToPublic, visitedDate);
        RsData imageSavingResult = imageService.saveImages(images, postId);
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

        List<Comment> comments = commentService.findNotDeleted(postId);
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("modify/{postId}")
    public String modifyPost(@PathVariable long postId, Model model) {
        long userId = rq.getMember().getId();

        RsData postOwnerValidation = postService.validPostOwner(userId, postId);
        if (postOwnerValidation.isFail()) {
            return postOwnerValidation.getMsg();
        }

        RsData<Viewer> response = postService.showSinglePost(postId);
        model.addAttribute("modifyingData", response);
        return "/usr/post/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("modify/{postId}")
    public String modifyPost(@Valid ModifyingForm modifyingForm, @PathVariable long postId) {
        long userId = rq.getMember().getId();

        RsData postOwnerValidation = postService.validPostOwner(userId, postId);
        if (postOwnerValidation.isFail()) {
            return postOwnerValidation.getMsg();
        }

        long placeId = postService.convertPlaceToId(modifyingForm.getPlace());
        String content = modifyingForm.getContent();
        LocalDate visitedDate = modifyingForm.getVisitedDate();
        postService.modifyContent(postId, placeId, content, visitedDate);

        return "redirect:/post/list";
    }

}