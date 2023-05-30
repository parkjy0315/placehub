package com.placehub.base.initData;

import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
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
            MemberService memberService
    ) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                // Member memberAdmin = memberService.join("admin", "1234").getData();
                Member member1 = memberService.join("user1", "1234", "123@123", "이름1", "닉네임1").getData();
                Member member2 = memberService.join("user2", "1234", "234@234", "이름12", "닉네임2").getData();
            }
        };
    }
}