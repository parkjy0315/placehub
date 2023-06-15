package com.placehub.boundedContext.post.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.place.dto.PlaceInfo;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.repository.PlaceRepository;
import com.placehub.boundedContext.place.service.PlaceInfoService;
import com.placehub.boundedContext.place.service.PlaceService;
import com.placehub.boundedContext.post.form.CreatingForm;
import com.placehub.boundedContext.post.form.ModifyingForm;
import com.placehub.boundedContext.post.form.Viewer;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceService placeService;
    @Autowired
    private PlaceInfoService placeInfoService;
    @Autowired
    private MemberService memberService;
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
        if (!creatingForm.getImgIds().equals("")) {
            RsData imgSavingResult = imageService.createOrModifyImages(Arrays.stream(creatingForm.getImgIds().split(",")).map(Long::parseLong).toList(), postId);

            if (imgSavingResult.isFail()) {
                return imgSavingResult;
            }
        }
        return RsData.of("S-1", "아카이빙이 등록되었습니다", postId);
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

        RsData imgModifyingResult = imageService.createOrModifyImages(checkEmpty(modifyingForm), postId);

        if (imgModifyingResult.isFail()) {
            throw new RuntimeException("이미지 저장 오류");
        }
        return postRepository.save(post).getId();

    }

    private List<Long> checkEmpty(ModifyingForm modifyingForm) {
        if (modifyingForm.getImgIds().equals("")) {
            return new ArrayList<Long>();
        }

        return Arrays.stream(modifyingForm.getImgIds().split(",")).map(Long::parseLong).toList();
    }

    public RsData<String> displayPlaceDuringCreating(long placeId) {
        return RsData.of("S-1", "장소명 확인 성공", placeRepository.findById(placeId).get().getPlaceName());
    }

    public RsData<Viewer> showSinglePost(long postId) {
        Post post = findById(postId).orElse(null);

        if (post == null) {
            return RsData.of("F-2", "존재하지 않는 포스팅입니다");
        }

        Member member = memberService.findById(post.getMember()).orElse(null);

        List<String> imagePathes = imageService.callImagePathes(postId);
        String mainImage = "";
        if(imagePathes.size() > 0){
            mainImage = imagePathes.get(0);
        }

        Place place = placeService.getPlace(post.getPlace());
        PlaceInfo placeInfo = placeInfoService.getCategoryNames(place);

        Viewer viewer = Viewer.builder()
                .userId(post.getMember())
                .username(member.getNickname())
                .placeId(post.getPlace())
                .placeName(placeService.getPlace(post.getPlace()).getPlaceName())
                .postId(postId)
                .createDate(post.getCreateDate())
                .visitedDate(post.getVisitedDate())
                .content(post.getContent())
                .isOpenToPublic(post.isOpenToPublic())
                .mainImage(mainImage)
                .bigCategoryName(placeInfo.getBigCategoryName())
                .midCategoryName(placeInfo.getMidCategoryName())
                .smallCategoryName(placeInfo.getSmallCategoryName())
                .build();

        return RsData.of("S-1", "게시글 페이지 응답", viewer);
    }


    @Transactional
    public RsData<Post> deletePost(long postId) {
        Optional<Post> wrappedPost = postRepository.findById(postId);

        if (wrappedPost.isEmpty()) {
            return RsData.of("F-2", "존재하지 않는 포스팅입니다");
        }

        Post post = wrappedPost.get().toBuilder()
                .deleteDate(LocalDateTime.now())
                .build();

        postRepository.save(post);

        RsData<Post> imagDeleteResult = imageService.deleteAllInPost(postId);

        if (imagDeleteResult.isFail()) {
            return imagDeleteResult;
        }

        return RsData.of("S-1", "아카이빙이 삭제되었습니다", post);
    }

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> findByMember(Long id, Pageable pageable) {
        return postRepository.findByMember(id, pageable);
    }

    public Page<Post> findByPlace(Long placeId, Pageable pageable) {
        return postRepository.findByPlace(placeId, pageable);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }
}