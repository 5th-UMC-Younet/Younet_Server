package com.example.younet.alarm.repository.CustomRepository;

import com.example.younet.alarm.dto.AlarmResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonAlarmRepositoryCustom {
    public Slice<AlarmResponseDTO.commonAlarmListResultDTO> getCommonAlarmList(Long lastAlarmId, Long receiverId, Pageable pageable);
    public Slice<AlarmResponseDTO.chatAlarmListResultDTO> getChatAlarmList(Long lastChatAlarmId, Long receiverId, Pageable pageable);
    }
