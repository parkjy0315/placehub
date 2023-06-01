package com.placehub.boundedContext.post.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.place.repository.PlaceRepository;
import com.placehub.boundedContext.post.form.Viewer;
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
    @Autowired
    private PlaceRepository placeRepository;

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

    public RsData validPostOwner(long userId, long postId) {
        Optional<Post> wrappedPost = postRepository.findById(postId);

        if (wrappedPost.isEmpty()) {
            return RsData.of("F-3", "존재하지 않는 게시글입니다");
        }

        Post post = wrappedPost.get();

        if (post.getMember() != userId) {
            return RsData.of("F-4", "이 게시글의 작성자가 아닙니다");
        }

        return RsData.of("S-1", "올바른 권한을 가진 이용자입니다");
    }

    private boolean validateCreatingPost(Long userId, Long placeId, LocalDate visitedDate) {
        LocalDate now = LocalDate.now();
        return !userId.equals(null) && !placeId.equals(null) && !visitedDate.isAfter(now);
    }

    private boolean validateModifyingPost(Long placeId, LocalDate visitedDate) {
        return !placeId.equals(null) && !visitedDate.isAfter(LocalDate.now());
    }

    public long convertPlaceToId(String place) {
        if (place.equals("서울 시청")) {
            return 1L;
        }

        return 2L;
    }

    public String convertIdToPlace(long placeId) {
        if (placeId == 1L) {
            return "서울 시청";
        }

        return "부산 시청";
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

    public long modifyContent(long postId, long placeId, String content, LocalDate visitedDate) throws RuntimeException{
        if (!validateModifyingPost(placeId, visitedDate)) {
            throw new RuntimeException("올바르지 않은 포스팅");
        }

        Optional<Post> wrappedPost = postRepository.findById(postId);
        Post post = wrappedPost.get();

        post = post.toBuilder()
                .place(placeId)
                .content(content)
                .visitedDate(visitedDate)
                .build();

        return postRepository.save(post).getId();

    }

    public RsData<Viewer> showSinglePost(long postid) {
        Viewer viewer = new Viewer();
        Optional<Post> tmpPost = postRepository.findById(postid);

        if (tmpPost.isEmpty()) {
            return RsData.of("F-2", "존재하지 않는 포스팅입니다");
        }

        Post post = tmpPost.get();
        Optional<Member> tmpMember = memberRepository.findById(post.getMember());
        Member member = tmpMember.get();

        viewer.setUsername(member.getNickname());
        viewer.setContent(post.getContent());
        viewer.setVisitedDate(post.getVisitedDate());
        viewer.setPostId(postid);
        viewer.setPlaceName(convertIdToPlace(post.getPlace()));
        viewer.setOpenToPublic(post.isOpenToPublic());
        return RsData.of("S-1", "게시글 페이지 응답", viewer);
    }

    public RsData deletePost(long postId) {
        Optional<Post> wrappedPost = postRepository.findById(postId);

        if (wrappedPost.isEmpty()) {
            return RsData.of("F-2", "존재하지 않는 포스팅입니다");
        }

        Post post = wrappedPost.get().toBuilder()
                .deleteDate(LocalDateTime.now())
                .build();

        postRepository.save(post);
        return RsData.of("S-1", "삭제 성공", post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }
}
