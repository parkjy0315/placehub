package com.placehub.boundedContext.placeLike.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.service.PlaceService;
import com.placehub.boundedContext.placelike.entity.PlaceLike;
import com.placehub.boundedContext.placelike.service.PlaceLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class PlaceLikeServiceTests {

    @Autowired
    private PlaceLikeService placeLikeService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PlaceService placeService;

    @Test
    @DisplayName("좋아요 등록")
    void t001() throws Exception {
        Member member = memberService.findById(1L).orElse(null);
        Place place = placeService.getPlace(1L);

        placeLikeService.create(place.getId(), member);

        PlaceLike placeLike = placeLikeService.findByPlaceIdAndMemberId(place.getId(), member.getId());

        assertThat(placeLike.getPlaceId()).isEqualTo(place.getId());
        assertThat(placeLike.getMemberId()).isEqualTo(member.getId());

    }

    @Test
    @DisplayName("좋아요 취소 - 권한 있음")
    void t002() throws Exception {
        Member member = memberService.findById(1L).orElse(null);
        Place place = placeService.getPlace(1L);

        placeLikeService.create(place.getId(), member);

        PlaceLike placeLike = placeLikeService.findByPlaceIdAndMemberId(place.getId(), member.getId());
        Long placeLikeId = placeLike.getId();

        RsData<PlaceLike> canDeleteResult = placeLikeService.canDelete(placeLike, member);
        assertThat(canDeleteResult.getResultCode()).isEqualTo("S-1");

        placeLikeService.delete(placeLike);
        placeLike = placeLikeService.findById(placeLikeId).orElse(null);
        assertThat(placeLike).isNull();
    }

    @Test
    @DisplayName("좋아요 취소 - 권한 없음")
    void t003() throws Exception {
        Member member1 = memberService.findById(1L).orElse(null);
        Member member2 = memberService.findById(2L).orElse(null);
        Place place = placeService.getPlace(1L);

        placeLikeService.create(place.getId(), member1);

        PlaceLike placeLike = placeLikeService.findByPlaceIdAndMemberId(place.getId(), member1.getId());

        RsData<PlaceLike> canDeleteResult = placeLikeService.canDelete(placeLike, member2);

        assertThat(canDeleteResult.getResultCode()).isEqualTo("F-1");
    }
}
