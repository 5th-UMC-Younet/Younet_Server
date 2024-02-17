package com.example.younet.repository;

import com.example.younet.domain.JoinChat;
import com.example.younet.domain.Post;
import com.example.younet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinChatRepository extends JpaRepository<JoinChat, Long> {

    List<JoinChat> findByUserId(Long userId);

    @Query(value = "SELECT * FROM join_chat WHERE (chat_room_chatroom_id = :chatRoomId AND user_id != :userId AND is_del=false)", nativeQuery = true)
    JoinChat findJoinChatByAnotherUser(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

}
