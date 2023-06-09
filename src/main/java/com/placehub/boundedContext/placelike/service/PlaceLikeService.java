package com.placehub.boundedContext.placelike.service;

import com.placehub.base.event.EventAfterUpdatePlaceLike;
import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.place.entity.Place;
import com.placehub.boundedContext.place.service.PlaceService;
import com.placehub.boundedContext.placelike.entity.PlaceLike;
import com.placehub.boundedContext.placelike.repository.PlaceLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceLikeService {

    private final PlaceLikeRepository placeLikeRepository;
    private final PlaceService placeService;
    private final ApplicationEventPublisher publisher;


    // 현재 상태 확인
    public RsData<PlaceLike> checkStatus(PlaceLike placeLike) {
        if(placeLike == null) {
            return RsData.of("F-2", "이미 취소된 좋아요입니다.");
        }
        return RsData.of("F-1", "이미 등록된 좋아요입니다.");
    }

    @Transactional
    public RsData<PlaceLike> create(Long placeId, Member actor) {

        Place place = placeService.getPlace(placeId);

        PlaceLike placeLike = PlaceLike.builder()
                .memberId(actor.getId())
                .placeId(placeId)
                .build();

        placeLikeRepository.save(placeLike);

        publisher.publishEvent(new EventAfterUpdatePlaceLike(this, placeLike.getPlaceId(), true));

        String placeName = place.getPlaceName();

        return RsData.of("S-1", "%s에 대한 좋아요가 등록되었습니다.".formatted(placeName), placeLike);
    }

    @Transactional
    public RsData<PlaceLike> delete(PlaceLike placeLike) {

        String placeName = placeService.getPlace(placeLike.getPlaceId()).getPlaceName();

        placeLikeRepository.delete(placeLike);

        publisher.publishEvent(new EventAfterUpdatePlaceLike(this, placeLike.getPlaceId(), false));

        return RsData.of("S-1", "%s에 대한 좋아요가 취소되었습니다.".formatted(placeName), placeLike);
    }


    public RsData<PlaceLike> canDelete(PlaceLike placeLike, Member actor) {

        long actorId = actor.getId();
        long fromMemberId = placeLike.getMemberId();

        if(actorId != fromMemberId){
            return RsData.of("F-1", "취소 권한이 없습니다");
        }

        return RsData.of("S-1", "취소 가능합니다.", placeLike);
    }


    public Optional<PlaceLike> findById(Long Id) {
        return placeLikeRepository.findById(Id);
    }

    public PlaceLike findByPlaceIdAndMemberId(Long placeId, Long memberId){
        return placeLikeRepository.findByPlaceIdAndMemberId(placeId, memberId);
    }

    public List<PlaceLike> findByPlaceId(Long placeId){
        return placeLikeRepository.findByPlaceId(placeId);
    }


}
