package com.placehub.boundedContext.comment.controller;

import com.placehub.base.rq.Rq;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
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
        return "redirect:/post/view/%s".formatted(postId);
    }

    @GetMapping("/list/{postId}")
    public String getList(Model model) {
        List<Comment> comments = commentService.findAll();
        model.addAttribute("comments", comments);
        return "/usr/comment/comment";
    }

    // 수정 폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update/{id}")
    public String update(Model model, @PathVariable Long id, @RequestParam Long postId) {

        Comment comment = commentService.findById(id).orElse(null);

        RsData<Comment> checkRsData = checkPermissionAndValidity(comment);

        if(checkRsData.isFail()){
            return rq.historyBack(checkRsData);
        }

        model.addAttribute("comment", comment);
        return "usr/comment/comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @RequestParam Long postId, @RequestParam String content) {

        Comment comment = commentService.findById(id).orElse(null);

        RsData<Comment> checkRsData = checkPermissionAndValidity(comment);

        if(checkRsData.isFail()){
            return rq.historyBack(checkRsData);
        }

        RsData<Comment> updateRsData = commentService.update(comment, content);

        return rq.redirectWithMsg("/post/view/%s".formatted(postId), updateRsData);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, @RequestParam Long postId) {
        Comment comment = commentService.findById(id).orElse(null);

        RsData<Comment> checkRsData = checkPermissionAndValidity(comment);

        if(checkRsData.isFail()){
            return rq.historyBack(checkRsData);
        }

        RsData<Comment> deleteRsData = commentService.delete(comment);

        return rq.redirectWithMsg("/post/view/%s".formatted(postId), deleteRsData);
    }

    private RsData<Comment> checkPermissionAndValidity(Comment comment){

        RsData<Comment> isVaildRsData = commentService.isVaild(comment);
        if(isVaildRsData.isFail()){
            return isVaildRsData;
        }

        return commentService.hasPermission(comment, rq.getMember());
    }
}
