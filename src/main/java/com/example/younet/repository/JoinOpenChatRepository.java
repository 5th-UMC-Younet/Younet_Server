package com.example.younet.repository;

import com.example.younet.domain.JoinOpenChat;
import com.example.younet.domain.Post;
import com.example.younet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinOpenChatRepository extends JpaRepository<JoinOpenChat, Long> {

    List<JoinOpenChat> findByUserId(Long userId);

    @Query("SELECT jc FROM JoinOpenChat jc WHERE (jc.openChatRoom.id = :chatRoomId AND jc.user.id != :userId)")
    JoinOpenChat findJoinOpenChatByAnotherUser(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Query("SELECT jc FROM JoinOpenChat jc WHERE (jc.openChatRoom.id = :chatRoomId)")
    List<JoinOpenChat> findJoinOpenChatsById(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT o FROM JoinOpenChat o WHERE o.openChatRoom.id = :chat_room_id AND o.user.id = :userId")
    JoinOpenChat findByOpenChatroomIdAndUserId(@Param("chat_room_id") Long chat_room_id, @Param("userId") Long userId);

}
