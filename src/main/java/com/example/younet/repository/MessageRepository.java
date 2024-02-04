package com.example.younet.repository;

import com.example.younet.domain.JoinChat;
import com.example.younet.domain.Message;
import com.example.younet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :chatRoomId ORDER BY m.createdAt DESC LIMIT 1")
    Message findLatestMessage(@Param("chatRoomId") Long chatRoomId);

}
