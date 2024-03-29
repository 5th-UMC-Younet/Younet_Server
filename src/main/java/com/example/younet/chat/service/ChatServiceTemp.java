package com.example.younet.chat.service;

import com.example.younet.alarm.repository.CommonAlarmRepository;
import com.example.younet.domain.ChatAlarm;
import com.example.younet.domain.ChatRequest;
import com.example.younet.domain.CommonAlarm;
import com.example.younet.domain.enums.AlarmType;
import com.example.younet.domain.enums.Profile;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.repository.ChatAlarmRepository;
import com.example.younet.repository.ChatRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceTemp {
    private final CommonAlarmRepository commonAlarmRepository;
    private final ChatAlarmRepository chatAlarmRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final ChatRequestRepository chatRequestRepository;
    public Long refuseChatRequest(Long chatAlarmId){
        ChatAlarm chatAlarm=chatAlarmRepository.findById(chatAlarmId).get();
        chatAlarm.setConfirmed(true);
        chatAlarmRepository.save(chatAlarm);
        ChatRequest chatRequest=chatRequestRepository.findById(chatAlarm.getChatRequest().getId()).get();
        chatRequest.setAccepted(false);

        String actorName;
        if (chatRequest.getProfile()== Profile.REALNAME)
            actorName= chatRequest.getReceiver().getName();
        else
            actorName= chatRequest.getReceiver().getNickname();
        commonAlarmRepository.save(
                CommonAlarm.builder()
                        .alarmType(AlarmType.CHAT)
                        .isConfirmed(false)
                        .receiver(communityProfileRepository.findByUserId(chatRequest.getRequester().getId()))
                        .actorName(actorName)
                        .build()
        );


        return chatAlarm.getReceiver().getId();
    }
}