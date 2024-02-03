package com.example.younet.alarm.repository;

import com.example.younet.domain.CommonAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonAlarmRepository extends JpaRepository<CommonAlarm, Long> {
}
