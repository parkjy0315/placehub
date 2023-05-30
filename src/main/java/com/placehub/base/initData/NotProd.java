package com.placehub.base.initData;

import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.service.PlaceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            PlaceService placeService
    ) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                // Member memberAdmin = memberService.join("admin", "1234").getData();
                Member member1 = memberService.join("user1", "1234", "123@123", "이름1", "닉네임1").getData();
                Member member2 = memberService.join("user2", "1234", "234@234", "이름12", "닉네임2").getData();

                Member memberAdmin = memberService.create("admin", "1234", "user1", "user1@gmail.com", "user1Nick");

                Place place = placeService.create(1L, 1L, 1L,
                        "place", "02-123-1234", "서울 ",
                        12.0, 123.0);

            }
        };
    }
}