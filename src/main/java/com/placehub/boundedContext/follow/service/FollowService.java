package com.placehub.boundedContext.follow.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.follow.entity.Follow;
import com.placehub.boundedContext.follow.repository.FollowRepository;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberService memberService;

    public RsData<Follow> follow(Long followerId, String nickname) {

        Member followingMember = memberService.findByNickname(nickname).orElse(null);

        if(followingMember == null){
            return RsData.of("F-1", "존재하지 않는 사용자입니다.");
        }

        if(followingMember.getId() == followerId){
            return RsData.of("F-2", "나를 팔로우할 수 없습니다.");
        }

        Follow follow = findByFollowerIdAndFollowingId(followerId, followingMember.getId()).orElse(null);

        if (follow != null) {
            return RsData.of("F-3", "이미 팔로우한 사용자입니다.");
        }

        Follow newFollow = Follow.builder()
                .followerId(followerId)
                .followingId(followingMember.getId())
                .build();

        followRepository.save(newFollow);
        return RsData.of("S-1", "%s님을 팔로우합니다.".formatted(followingMember.getNickname()));
    }

    public Optional<Follow> findById(Long id) {
        return followRepository.findById(id);
    }

    public Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId) {
        return followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
    }

    public Optional<Follow> findByFollowerId(Long followerId) {
        return followRepository.findByFollowerId(followerId);
    }

    public Optional<Follow> findByFollowingId(Long followingId) {
        return followRepository.findByFollowingId(followingId);
    }

    public List<Member> findFollowing(Long followerId) {
        return followRepository.findFollowingByFollowerId(followerId);
    }

    public List<Member> findFollower(Long followingId) {
        return followRepository.findFollowerByFollowingId(followingId);
    }
}
