package com.placehub.boundedContext.follow.repository;


import com.placehub.boundedContext.follow.entity.Follow;
import com.placehub.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Optional<Follow> findByFollowerId(Long followerId);
    Optional<Follow> findByFollowingId(Long followingId);

    @Query("SELECT m FROM Follow f JOIN Member m ON f.followingId = m.id WHERE f.followerId = :followerId")
    List<Member> findFollowingByFollowerId(@Param("followerId") Long followerId);

    @Query("SELECT m FROM Follow f JOIN Member m ON f.followerId = m.id WHERE f.followingId = :followingId")
    List<Member> findFollowerByFollowingId(@Param("followingId") Long followingId);
}
