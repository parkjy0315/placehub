package com.placehub.boundedContext.comment.controller;

import com.placehub.base.rq.Rq;
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
    private final PostService postService;
    private final Rq rq;

    // @PreAuthorize("isAuthenticated()")
//    @PostMapping("/create")
//    public String create(@RequestParam Long postId, @RequestParam String content){
//
//        Post post = postService.findById(postId).orElse(null);
//        commentService.create(post, content, rq.getMember());
//        return "/usr/comment/comment";
//    }

    @PostMapping("/create")
    public String create(@RequestParam Long postId, @RequestParam String content, @RequestParam String username){

        commentService.create(postId, content, username);
        return "redirect:/comment/list";
    }

//    @GetMapping("/list")
//    public String get(@PathVariable("id") Long id, Model model){
//        Comment comment = commentService.findById(id).orElse(null);
//        model.addAttribute("comment", comment);
//        return "/usr/comment/comment";
//    }

    @GetMapping("/list")
    public String getList(Model model) {
        List<Comment> comments = commentService.findAll();
        model.addAttribute("comments", comments);
        return "/usr/comment/comment";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        Comment comment = commentService.findById(id).orElse(null);
        model.addAttribute("comment", comment);
        return "usr/comment/comment_form"; // 수정 페이지 템플릿의 파일명
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @RequestParam String content) {
        commentService.update(id, content);
        return "redirect:/comment/list";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        commentService.delete(id);
        return "redirect:/comment/list";
    }
}
