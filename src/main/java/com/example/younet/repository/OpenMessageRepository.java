package com.example.younet.repository;

import com.example.younet.domain.OpenMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OpenMessageRepository extends JpaRepository<OpenMessage, Long> {
    @Query("SELECT m FROM OpenMessage m WHERE m.openChatRoom.id = :chatRoomId ORDER BY m.createdAt DESC LIMIT 1")
    OpenMessage findLatestMessage(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT m FROM OpenMessage m WHERE m.openChatRoom.id = :chatRoomId ORDER BY m.createdAt")
    List<OpenMessage> findMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
