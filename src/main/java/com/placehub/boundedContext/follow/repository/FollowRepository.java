package com.placehub.boundedContext.follow.repository;


import com.placehub.boundedContext.follow.entity.Follow;
import com.placehub.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Optional<Follow> findByFollowerId(Long followerId);
    Optional<Follow> findByFollowingId(Long followingId);


}
