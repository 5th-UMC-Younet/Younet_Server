package com.example.younet.alarm.service;

import com.example.younet.alarm.dto.AlarmResponseDTO;
import com.example.younet.alarm.repository.CommonAlarmRepository;
import com.example.younet.domain.CommonAlarm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmCommandService {
    private final CommonAlarmRepository commonAlarmRepository;

    public CommonAlarm updateIsConfirmed(Long alarmId){
        CommonAlarm commonAlarm=commonAlarmRepository.findById(alarmId).get();
        commonAlarm.setConfirmed(true);
        return commonAlarmRepository.save(commonAlarm);
    }

    public Slice<AlarmResponseDTO.commonAlarmListResultDTO> getCommonAlarmList(Long lastAlarmId,Long receiverId){
        Pageable pageable= PageRequest.of(0,10);
        return commonAlarmRepository.getCommonAlarmList(lastAlarmId,receiverId,pageable);
    }
}
