package com.example.younet.repository;

import com.example.younet.domain.ChatRoom;
import com.example.younet.domain.JoinChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    //먼저 메시지 생성 시간을 기준으로 최신순 정렬 -> 그 후 SELECT DISTINCT를 사용하여 중복된 ChatRoom 제거
//    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
//            "WHERE cr.id = ? " +
//            "AND cr IN (SELECT cr FROM ChatRoom cr " +
//            "JOIN Message m ON cr = m.chatRoom " +
//            "WHERE cr.id = ? " +
//            "ORDER BY m.createdAt DESC)")
//    List<ChatRoom> findByChatroomIdOrderByMessageCreatedAtDesc(Long chatroomId);
//

//    @Query("SELECT DISTINCT cr FROM ChatRoom cr WHERE cr.id = :chatRoomId AND cr IN (SELECT cr FROM ChatRoom cr JOIN Message m ON cr = m.chatRoom WHERE cr.id = :chatRoomId ORDER BY m.createdAt DESC)")
//    List<ChatRoom> findByChatroomIdOrderByMessageCreatedAtDesc(@Param("chatRoomId") Long chatRoomId);


    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
            "JOIN ChatMessage m ON m.chatRoom.id = cr.id " +
            "WHERE cr.id IN " +
            "(SELECT m.chatRoom.id FROM ChatMessage m GROUP BY m.chatRoom.id HAVING MAX(m.createdAt) IS NOT NULL) " +
            "ORDER BY (SELECT MAX(msg.createdAt) FROM ChatMessage msg WHERE msg.chatRoom.id = cr.id) DESC")
    List<ChatRoom> findByChatroomIdOrderByMessageCreatedAtDesc(@Param("chatRoomId") Long chatRoomId);

}
