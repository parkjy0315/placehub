package com.placehub.boundedContext.comment.service;

import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.repository.CommentRepository;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    public Comment create(Post post, String content, Member author){

        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .author(author)
                .build();

        return commentRepository.save(comment);
    }

    public Comment get(Long id){
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElse(null);
    }

    public Comment update(Long id, String content){
        Comment comment = commentRepository.findById(id).orElse(null);
        Comment updateComment = Comment.builder()
                .content(content)
                .build();
        return commentRepository.save(updateComment);
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }


}
