package com.example.younet.alarm.service;

import com.example.younet.alarm.repository.CommonAlarmRepository;
import com.example.younet.domain.CommonAlarm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmCommandService {
    private final CommonAlarmRepository commonAlarmRepository;

    public long updateIsConfirmed(Long alarmId){
        CommonAlarm commonAlarm=commonAlarmRepository.findById(alarmId).get();
        commonAlarm.setConfirmed(true);
        commonAlarmRepository.save(commonAlarm);
        return commonAlarm.getPostId();
    }

}
