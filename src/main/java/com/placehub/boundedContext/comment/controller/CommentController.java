package com.placehub.boundedContext.comment.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.service.CommentService;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{postId}")
    public String create(@PathVariable("postId") String postId ,@RequestParam String content){
        Long parsedPostId = Long.parseLong(postId);
        commentService.create(parsedPostId, content, rq.getMember());
        return "redirect:/post/view/" + postId;
    }

    @GetMapping("/list/{postId}")
    public String getList(Model model) {
        List<Comment> comments = commentService.findAll();
        model.addAttribute("comments", comments);
        return "/usr/comment/comment";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        Comment comment = commentService.findById(id).orElse(null);
        model.addAttribute("comment", comment);
        return "usr/comment/comment_form"; // 수정 페이지 템플릿의 파일명
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @RequestParam String content) {
        Long postId = commentService.findById(id).get().getPostId();
        commentService.update(id, content);
        return "redirect:/post/view/" + postId;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Long postId = commentService.findById(id).get().getPostId();

        RsData<Comment> isVaildRsData = commentService.isVaild(id);
        if(isVaildRsData.isFail()){
            return rq.redirectWithMsg("/post/view/" + postId, isVaildRsData);
        }

        RsData<Comment> hasPermissionRsData = commentService.hasPermission(id, rq.getMember());
        if(hasPermissionRsData.isFail()){
            return rq.redirectWithMsg("/post/view/" + postId, hasPermissionRsData);
        }

        RsData<Comment> deleteRsData = commentService.delete(id);

        return rq.redirectWithMsg("/post/view/" + postId, deleteRsData);
    }
}
