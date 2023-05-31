package com.placehub.boundedContext.post.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.form.Viewer;
import com.placehub.boundedContext.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final Rq rq;
    private final PostService postService;
    @Data
    class  PostForm {
        private String place;
//        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/MM/dd")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate visitedDate;
        @NotBlank
        private String isOpenToPublic;
        private String content;
    }

    @GetMapping("/makePost")
    public String makepost() {
        return "posts/makePost";
    }

//    @PreAuthorize("isAuthenticated()")
    @PostMapping("/makePost")
    public String test(@Valid PostForm postForm) {
        long userId = 1L;
        long placeId = postService.convertPlaceToId(postForm.getPlace());
        boolean isOpenToPublic = postForm.getIsOpenToPublic().equals("공개");
        String content = postForm.getContent();
        LocalDate visitedDate = postForm.getVisitedDate();

        postService.createPost(userId, placeId, content, isOpenToPublic, visitedDate);
        return "redirect:/posts/makePost";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/showPost/{id}")
    public String showPost(@PathVariable long postId, Model model) {
        RsData<Viewer> response = postService.showSinglePost(postId);

        if (response.isFail()) {
            return rq.historyBack(response);
        }

        model.addAttribute("postView", response);
        return null;
    }
}
