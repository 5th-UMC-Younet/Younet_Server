package com.example.younet.chat.controller;

import com.example.younet.ApiPayload.ApiResponse;
import com.example.younet.chat.service.ChatServiceTemp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatTemp")
public class ChatControllerTemp {
    private final ChatServiceTemp chatServiceTemp;
    @GetMapping("/refuse/{chatAlarmId}")
    public ApiResponse<?> refuseChatRequest(@PathVariable("chatAlarmId") Long chatAlarmId){

        return ApiResponse.onSuccess(HttpStatus.OK,chatServiceTemp.refuseChatRequest(chatAlarmId));
        // 채팅 요청 알람 목록 조회 api 구현 후 redirect로 바꾸기
    }
}
