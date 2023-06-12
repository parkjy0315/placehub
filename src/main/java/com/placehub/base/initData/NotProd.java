package com.placehub.base.initData;

import com.placehub.boundedContext.category.entity.BigCategory;
import com.placehub.boundedContext.category.entity.MidCategory;
import com.placehub.boundedContext.category.entity.SmallCategory;
import com.placehub.boundedContext.category.service.BigCategoryService;
import com.placehub.boundedContext.category.service.MidCategoryService;
import com.placehub.boundedContext.category.service.SmallCategoryService;
import com.placehub.boundedContext.comment.entity.Comment;
import com.placehub.boundedContext.comment.service.CommentService;
import com.placehub.boundedContext.friend.service.FriendService;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.service.PlaceService;
import com.placehub.boundedContext.post.form.CreatingForm;
import com.placehub.boundedContext.post.service.PostService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
    @Bean
    @Order(1)
    CommandLineRunner initData(
            MemberService memberService,
            PlaceService placeService,
            BigCategoryService bigCategoryService,
            MidCategoryService midCategoryService,
            SmallCategoryService smallCategoryService,
            PostService postService,
            CommentService commentService,
            FriendService friendService
    ) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                // Member memberAdmin = memberService.join("admin", "1234").getData();
                Member member1 = memberService.join("user1", "1234", "123@123", "이름1", "닉네임1").getData();
                Member member2 = memberService.join("user2", "1234", "234@234", "이름2", "닉네임2").getData();
                Member member3 = memberService.join("user3", "1234", "345@345", "이름3", "닉네임3").getData();

                Member memberJinyeongKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__2812333976", "pjy100402@naver.com", "박진영", "KAKAO__2812333976").getData();

                Member memberAdmin = memberService.create("admin", "1234", "user1", "user1@gmail.com", "user1Nick");

                //
                BigCategory bigCategory = bigCategoryService.create("여행");
                MidCategory midCategory = midCategoryService.create("관광,명소", bigCategory.getId());
                SmallCategory smallCategory1 = smallCategoryService.create("계곡", midCategory.getId());
                SmallCategory smallCategory2 = smallCategoryService.create("산", midCategory.getId());
                SmallCategory smallCategory9 = smallCategoryService.create("온천", midCategory.getId());
                SmallCategory smallCategory12 = smallCategoryService.create("수목원,식물원", midCategory.getId());

                Place place = placeService.create(1L, 1L, 1L,
                        25235514L, "무당골", "", "경기 고양시 덕양구 내유동",
                        126.85496334236, 37.7177473046135);
                Place place2 = placeService.create(1L, 1L, 2L,
                        25374072L, "철마산", "", "경기 고양시 덕양구 내유동",
                        126.870253902635, 37.7194409312817);
                Place place3 = placeService.create(1L, 1L, 9L,
                        1896911677L, "유황온천로데오", "031-528-4310", "경기 남양주시 별내동 1006-1",
                        127.125620213198, 37.6457418256592);
                Place place4 = placeService.create(1L, 1L, 12L,
                        10442721L, "산들소리", "010-3039-3252", "경기 남양주시 별내동 785-1",
                        127.1015513297845, 37.65107359115661);


//                CreatingForm sampleCreatingForm = new CreatingForm();
//                sampleCreatingForm.setContent("테스트용 게시물입니다");
//                sampleCreatingForm.setIsOpenToPublic("공개");
//                sampleCreatingForm.setVisitedDate(LocalDate.now());
//                //Post post1 =
//                        postService.createPost(memberJinyeongKakao.getId(), place.getId(), sampleCreatingForm);
//
//                Comment comment = commentService.create(1L, "테스트 댓글 1", member1);


                friendService.follow(2L, "닉네임3");
                friendService.follow(3L, "닉네임2");
            }
        };
    }
}