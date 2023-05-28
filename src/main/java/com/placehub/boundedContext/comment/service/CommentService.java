package com.placehub.boundedContext.comment.service;

import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.repository.CommentRepository;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

//    public Comment create(Post post, String content, Member author){
//
//        Comment comment = Comment.builder()
//                .content(content)
//                .post(post)
//                .author(author)
//                .build();
//
//        return commentRepository.save(comment);
//    }

    public Comment create(Long postId, String content, String username){

        Comment comment = Comment.builder()
                .content(content)
                .postId(postId)
                .username(username)
                .build();

        return commentRepository.save(comment);
    }

    public Comment get(Long id){
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElse(null);
    }

    public Comment update(Long id, String content){
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return null;
        }
        Comment updatedComment = comment.toBuilder()
                .content(content)
                .build();
        return commentRepository.save(updatedComment);
    }

    public void delete(Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment != null) {
            commentRepository.delete(comment);
        }
    }


    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }
}
