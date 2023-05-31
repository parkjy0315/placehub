package com.placehub.boundedContext.post.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.form.Viewer;
import com.placehub.boundedContext.member.repository.MemberRepository;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    public long createPost(Long userId, Long placeId, String content, boolean openToPublic, LocalDate visitedDate) throws RuntimeException {
        if (!validateCreatingPost(userId, placeId, visitedDate)) {
            throw new RuntimeException("올바르지 않은 포스팅");
        }

        Post post = Post.builder()
                .member(userId)
                .place(placeId)
                .content(content)
                .openToPublic(openToPublic)
                .visitedDate(visitedDate)
                .build();

        return postRepository.save(post).getId();
    }

    private boolean validateCreatingPost(Long userId, Long placeId, LocalDate visitedDate) {
        LocalDate now = LocalDate.now();
        return !userId.equals(null) && !placeId.equals(null) && !visitedDate.isAfter(now);
    }

    public long convertPlaceToId(String place) {
        if (place.equals("서울 시청")) {
            return 1L;
        }

        return 2L;
    }

    public List<Post> getPostsByPlace(long placeId) {
        Optional<List<Post>> postList = postRepository.findPostsByPlace(placeId);

        if (postList.isPresent()) {
            List<Post> posts = postList.get();
            Collections.sort(posts);
            return posts;
        }

        return new ArrayList<>();
    }

    public long changePublicShowing(long id, boolean toChange) throws SQLException {
        Optional<Post> wrappedPost = postRepository.findById(id);

        if (wrappedPost.isPresent()) {
            Post post = wrappedPost.get();
            post = post.toBuilder()
                    .openToPublic(toChange)
                    .build();

            return postRepository.save(post).getId();
        }

        throw new SQLDataException("존재하지 않는 포스트입니다");
    }

    public long modifyContent(long id, String conent) throws SQLException {
        Optional<Post> wrappedPost = postRepository.findById(id);

        if (wrappedPost.isPresent()) {
            Post post = wrappedPost.get();

            post = post.toBuilder()
                    .content(conent)
                    .build();

            return postRepository.save(post).getId();
        }

        throw new SQLDataException("존재하지 않는 게시글입니다");
    }

    public RsData<Viewer> showSinglePost(long postID) {
        Viewer viewer = new Viewer();
        Optional<Post> tmpPost = postRepository.findById(postID);

        if (tmpPost.isEmpty()) {
            return RsData.of("F-2", "존재하지 않는 포스팅입니다");
        }

        Post post = tmpPost.get();
        Optional<Member> tmpMember = memberRepository.findById(post.getMember());
        Member member = tmpMember.get();

        viewer.setUsername(member.getNickname());
        viewer.setContent(post.getContent());
        viewer.setVisitedDate(post.getVisitedDate());
        return RsData.of("S-1", "게시글 페이지 응답", viewer);
    }
}
