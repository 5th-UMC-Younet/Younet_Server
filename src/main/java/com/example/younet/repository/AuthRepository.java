package com.example.younet.repository;

import com.example.younet.domain.Auth;
import com.example.younet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
}
