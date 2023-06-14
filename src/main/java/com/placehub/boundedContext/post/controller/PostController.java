package com.placehub.boundedContext.post.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.service.CommentService;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.form.*;
import com.placehub.boundedContext.post.service.ImageService;
import com.placehub.boundedContext.post.service.PostService;
import jakarta.validation.Valid;
import lombok.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final Rq rq;
    private final PostService postService;
    private final CommentService commentService;
    private final ImageService imageService;
    @Value("${custom.site.baseUrl}")
    public String baseUrl;


    @GetMapping("/create/{placeId}")
    public String create(Model model, @PathVariable("placeId") long placeId) {
        RsData<String> placeName = postService.displayPlaceDuringCreating(placeId);
        model.addAttribute("placeName", placeName);
        model.addAttribute("baseUrl", baseUrl);
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
    public String list(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "7") int size){

        // 페이징 정보
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> postPages =  this.postService.findAll(pageable);
        List<Post> postList = postPages.getContent();

        List<Viewer> postViewerList = new ArrayList<>();
        for (Post post : postList) {
            postViewerList.add(postService.showSinglePost(post.getId()).getData());
        }

        model.addAttribute("paging", postPages);
        model.addAttribute("postList", postList);
        model.addAttribute("postViewerList", postViewerList);

        // 페이징 정보
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", postPages.getTotalPages());
        model.addAttribute("totalElements", postPages.getTotalElements());


        return "usr/post/list";
    }

    @GetMapping("/view/{postId}")
    public String showPost(@PathVariable Long postId, Model model) {
        RsData<Viewer> response = postService.showSinglePost(postId);

        if (response.isFail()) {
            throw new RuntimeException("존재하지 않는 포스팅입니다");
        }

        List<Comment> comments = commentService.findNotDeleted(postId);
        List<String> imagePathes = imageService.callImagePathes(postId);
        model.addAttribute("comments", comments);

        model.addAttribute("postView", response.getData());
        model.addAttribute("imageList", imagePathes);
        model.addAttribute("baseUrl", baseUrl);
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
        model.addAttribute("baseUrl", baseUrl);
        return "usr/post/create";
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
        return rq.redirectWithMsg("/post/view/%s".formatted(postId), "아카이빙이 수정되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/get_Pre_Signed_Url/{flag}")
    @ResponseBody
    public List<PreSignedUrlResponseForm> createPreSigned(@RequestBody List<PreSignedUrlRequestForm> inputImgNames,
                                                            @PathVariable("flag") long flag) {
        List<PreSignedUrlResponseForm> preSignedUrl = imageService.getPreSignedUrlFromFilteredData(inputImgNames, flag);
        return preSignedUrl;
    }

}