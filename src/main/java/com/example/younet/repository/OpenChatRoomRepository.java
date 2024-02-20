package com.example.younet.repository;

import com.example.younet.domain.OpenChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OpenChatRoomRepository extends JpaRepository<OpenChatRoom, Long> {
    @Query("SELECT o FROM OpenChatRoom o " +
            "WHERE o.title LIKE CONCAT('%', :search, '%') ")
    List<OpenChatRoom> findByTitle(@Param("search") String search);
}
