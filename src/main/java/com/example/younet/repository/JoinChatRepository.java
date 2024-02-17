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

    @Query("SELECT jc FROM JoinChat jc WHERE (jc.chatRoom.id = :chatRoomId AND jc.user.id != :userId AND jc.isDel=false)")
    JoinChat findJoinChatByAnotherUser(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

}
