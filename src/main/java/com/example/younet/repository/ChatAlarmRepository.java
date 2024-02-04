package com.example.younet.repository;

import com.example.younet.domain.ChatAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatAlarmRepository extends JpaRepository<ChatAlarm,Long> {
}
