package com.example.younet.repository;

import com.example.younet.domain.ChatMessage;
import com.example.younet.domain.OpenChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId ORDER BY m.createdAt DESC LIMIT 1")
    ChatMessage findLatestMessage(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId ORDER BY m.createdAt")
    List<ChatMessage> findMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.chatRoom.id = :chatRoomId AND m.message LIKE CONCAT('%', :search, '%')")
    List<ChatMessage> findBySearch(@Param("chatRoomId") Long chatRoomId, @Param("search") String search);
}
