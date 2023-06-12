package com.placehub.boundedContext.comment.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.repository.CommentRepository;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public Comment create(Long postId, String content, Member actor){

        Long actorId = actor.getId();
        String actorNickName = actor.getNickname();

        Comment comment = Comment.builder()
                .content(content)
                .postId(postId)
                .memberId(actorId)
                .memberNickName(actorNickName)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional
    public RsData<Comment> update(Comment comment, String content){

        comment = comment.toBuilder().content(content).build();
        commentRepository.save(comment);

        return RsData.of("S-1", "댓글이 수정되었습니다.", comment);
    }

    //Soft Delete로 구현
    @Transactional
    public RsData<Comment> delete(Comment comment) {

        comment = comment.toBuilder().deleteDate(LocalDateTime.now()).build();
        commentRepository.save(comment);

        return RsData.of("S-1", "댓글이 삭제되었습니다.");
    }

    // 유효한 댓글인지 체크
    public RsData<Comment> isVaild(Comment comment) {

        if (comment == null) {
            return RsData.of("F-2", "존재하지 않는 댓글입니다.");
        }

        if (comment.getDeleteDate() != null) {
            return RsData.of("F-1", "삭제된 댓글입니다.");
        }

        return RsData.of("S-1", "유효한 댓글입니다.", comment);
    }

    // 권한 체크
    public RsData<Comment> hasPermission(Comment comment, Member actor) {
        if (comment.getMemberId() != actor.getId()) {
            return RsData.of("F-1", "수정 및 삭제 권한이 없습니다.");
        }
        return RsData.of("S-1","수정 및 삭제가 가능합니다.", comment);
    }


    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public List<Comment> findNotDeleted(Long postId) {
        return commentRepository.findByPostIdAndDeleteDateIsNull(postId);
    }
}
