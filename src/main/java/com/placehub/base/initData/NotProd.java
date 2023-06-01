package com.placehub.base.initData;

import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.service.PlaceService;
import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.service.PostService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            PlaceService placeService,
            PostService postService
    ) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                // Member memberAdmin = memberService.join("admin", "1234").getData();
                Member member1 = memberService.join("user1", "1234", "123@123", "이름1", "닉네임1").getData();
                Member member2 = memberService.join("user2", "1234", "234@234", "이름12", "닉네임2").getData();

                Member memberJinyeongKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__2812333976", "pjy100402@naver.com", "박진영", "박진영").getData();

                Member memberAdmin = memberService.create("admin", "1234", "user1", "user1@gmail.com", "user1Nick");

                Place place = placeService.create(1L, 1L, 1L,
                        "place", "02-123-1234", "서울 ",
                        12.0, 123.0);

                //Post post1 =
                        postService.createPost(memberJinyeongKakao.getId(), place.getId(),
                        "테스트용 게시물입니다.", true, LocalDate.now());
            }
        };
    }
}