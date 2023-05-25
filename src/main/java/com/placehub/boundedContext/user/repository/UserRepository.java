package com.placehub.boundedContext.user.repository;

import com.placehub.boundedContext.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findbyUsername(String username);
}
