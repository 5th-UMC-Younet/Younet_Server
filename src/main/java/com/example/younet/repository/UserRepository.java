package com.example.younet.repository;

import com.example.younet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long userId);

    Optional<User> findByUserLoginId(String userLoginId);

    Optional<User> findByNameAndEmail(String name, String email);

    boolean existsByEmail(String email);
}
