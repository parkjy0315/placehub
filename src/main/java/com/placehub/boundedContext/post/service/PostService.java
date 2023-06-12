package com.placehub.boundedContext.post.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.place.repository.PlaceRepository;
import com.placehub.boundedContext.post.form.CreatingForm;
import com.placehub.boundedContext.post.form.ModifyingForm;
import com.placehub.boundedContext.post.form.Viewer;
import com.placehub.boundedContext.member.repository.MemberRepository;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private ImageService imageService;

    private static String openToPublic = "공개";

    @Transactional
    public RsData createPost(Long userId, Long placeId, CreatingForm creatingForm) throws RuntimeException {
        if (!validateCreatingPost(userId, placeId, creatingForm.getVisitedDate())) {
            throw new RuntimeException("올바르지 않은 포스팅");
        }

        Post post = Post.builder()
                .member(userId)
                .place(placeId)
                .content(creatingForm.getContent())
                .openToPublic(creatingForm.getIsOpenToPublic().equals(openToPublic))
                .visitedDate(creatingForm.getVisitedDate())
                .build();

        long postId = postRepository.save(post).getId();
        RsData imgSavingResutl = imageService.controlImage(creatingForm.getImages(), postId, ImageControlOptions.CREATE);
        if (imgSavingResutl.isFail()) {
            return imgSavingResutl;
        }

        return RsData.of("S-1", "게시물 등록 성공", postId);
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

    private boolean validateModifyingPost(LocalDate visitedDate) {
        return !visitedDate.isAfter(LocalDate.now());
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

    @Transactional
    public long modifyContent(long postId, ModifyingForm modifyingForm) throws RuntimeException{
        if (!validateModifyingPost(modifyingForm.getVisitedDate())) {
            throw new RuntimeException("올바르지 않은 포스팅");
        }

        Optional<Post> wrappedPost = postRepository.findById(postId);
        Post post = wrappedPost.get();

        post = post.toBuilder()
                .content(modifyingForm.getContent())
                .visitedDate(modifyingForm.getVisitedDate())
                .build();

        RsData imgModifyingResult = imageService.controlImage(modifyingForm.getImages(), postId, ImageControlOptions.MODIFY);

        return postRepository.save(post).getId();

    }

    public RsData<String> displayPlaceDuringCreating(long placeId) {
        return RsData.of("S-1", "장소명 확인 성공", placeRepository.findById(placeId).get().getPlaceName());
    }

    public RsData<Viewer> showSinglePost(long postId) {
        Viewer viewer = new Viewer();
        Optional<Post> tmpPost = postRepository.findById(postId);

        if (tmpPost.isEmpty()) {
            return RsData.of("F-2", "존재하지 않는 포스팅입니다");
        }

        Post post = tmpPost.get();
        Optional<Member> tmpMember = memberRepository.findById(post.getMember());
        Member member = tmpMember.get();

        viewer.setUsername(member.getNickname());
        viewer.setContent(post.getContent());
        viewer.setVisitedDate(post.getVisitedDate());
        viewer.setPostId(postId);
        viewer.setPlaceName(placeRepository.findById(post.getPlace()).get().getPlaceName());
        viewer.setOpenToPublic(post.isOpenToPublic());
        viewer.setMember(post.getMember());
        viewer.setPlace(post.getPlace());

        return RsData.of("S-1", "게시글 페이지 응답", viewer);
    }

    @Transactional
    public RsData deletePost(long postId) {
        Optional<Post> wrappedPost = postRepository.findById(postId);

        if (wrappedPost.isEmpty()) {
            return RsData.of("F-2", "존재하지 않는 포스팅입니다");
        }

        Post post = wrappedPost.get().toBuilder()
                .deleteDate(LocalDateTime.now())
                .build();

        postRepository.save(post);

        RsData imagDeleteResult = imageService.deleteAllInPost(postId);

        if (imagDeleteResult.isFail()) {
            return imagDeleteResult;
        }

        return RsData.of("S-1", "삭제 성공", post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }
    public List<Post> findByMember(Long memberId) {
        return postRepository.findByMember(memberId);
    }
    public List<Post> findByPlace(Long placeId) {
        return postRepository.findByPlace(placeId);
    }
}