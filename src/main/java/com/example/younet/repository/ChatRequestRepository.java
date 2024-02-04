package com.example.younet.repository;

import com.example.younet.domain.ChatRequest;
import com.example.younet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {

    boolean existsByRequesterAndReceiver(User requester, User receiver);
}
