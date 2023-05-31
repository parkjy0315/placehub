package com.placehub.boundedContext.placelike.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.repository.PlaceRepository;
import com.placehub.boundedContext.place.service.PlaceService;
import com.placehub.boundedContext.placelike.entity.PlaceLike;
import com.placehub.boundedContext.placelike.repository.PlaceLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceLikeService {

    private final PlaceLikeRepository placeLikeRepository;
    private final PlaceService placeService;
    private final PlaceRepository placeRepository;


    public RsData<PlaceLike> create(Long placeId, Member actor) {

        //TODO : 서비스로 등록
        //Place place = placeService.findById(placeId).orElse(null);
        Place place = placeRepository.findById(placeId).orElse(null);

        PlaceLike placeLike = PlaceLike.builder()
                .member(actor)
                .place(place)
                .build();

        placeLikeRepository.save(placeLike);

        //TODO : member와 place에 등록

        String placeName = place.getPlaceName();

        return RsData.of("S-1", "%s에 대한 좋아요가 등록되었습니다.".formatted(placeName));
    }




}
