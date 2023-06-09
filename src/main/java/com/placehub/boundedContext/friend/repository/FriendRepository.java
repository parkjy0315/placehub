package com.placehub.boundedContext.friend.repository;


import com.placehub.boundedContext.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom {
    Optional<Friend> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Optional<Friend> findByFollowerId(Long followerId);
    Optional<Friend> findByFollowingId(Long followingId);


}
