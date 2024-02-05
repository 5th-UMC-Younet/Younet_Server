package com.example.younet.alarm.dto;

import com.example.younet.domain.enums.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AlarmResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class commonAlarmListResultDTO{
        Long alarmId;
        AlarmType alarmType;
        Long postId;
        Long actorId;
        LocalDateTime createdAt;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class chatAlarmListResultDTO{
        Long chatAlarmId;
        String requesterName;
        LocalDateTime createdAt;
    }

}
