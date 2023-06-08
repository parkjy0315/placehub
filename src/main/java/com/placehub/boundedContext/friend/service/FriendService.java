package com.placehub.boundedContext.friend.service;

import com.placehub.base.rsData.RsData;
import com.placehub.boundedContext.friend.entity.Friend;
import com.placehub.boundedContext.friend.repository.FriendRepository;
import com.placehub.boundedContext.member.entity.Member;
import com.placehub.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberService memberService;

    public RsData<Friend> follow(Long followerId, String nickname) {

        Member followingMember = memberService.findByNickname(nickname).orElse(null);

        if(followingMember == null){
            return RsData.of("F-1", "존재하지 않는 사용자입니다.");
        }

        if(followingMember.getId() == followerId){
            return RsData.of("F-2", "나를 팔로우할 수 없습니다.");
        }

        Friend friend = findByFollowerIdAndFollowingId(followerId, followingMember.getId()).orElse(null);

        if (friend != null) {
            return RsData.of("F-3", "이미 팔로우한 사용자입니다.");
        }

        Friend newFriend = Friend.builder()
                .followerId(followerId)
                .followingId(followingMember.getId())
                .build();

        friendRepository.save(newFriend);
        return RsData.of("S-1", "%s님을 팔로우합니다.".formatted(followingMember.getNickname()));
    }

    public Optional<Friend> findById(Long id) {
        return friendRepository.findById(id);
    }

    public Optional<Friend> findByFollowerIdAndFollowingId(Long followerId, Long followingId) {
        return friendRepository.findByFollowerIdAndFollowingId(followerId, followingId);
    }

    public Optional<Friend> findByFollowerId(Long followerId) {
        return friendRepository.findByFollowerId(followerId);
    }

    public Optional<Friend> findByFollowingId(Long followingId) {
        return friendRepository.findByFollowingId(followingId);
    }

    public List<Member> findFollowing(Long followerId) {
        return friendRepository.findFollowingByFollowerId(followerId);
    }

    public List<Member> findFollower(Long followingId) {
        return friendRepository.findFollowerByFollowingId(followingId);
    }
}
