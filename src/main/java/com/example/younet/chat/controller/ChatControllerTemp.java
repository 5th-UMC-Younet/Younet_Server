package com.example.younet.chat.controller;

import com.example.younet.ApiPayload.ApiResponse;
import com.example.younet.chat.service.ChatServiceTemp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatTemp")
public class ChatControllerTemp {
    private final ChatServiceTemp chatServiceTemp;
    @GetMapping("/refuse/{chatAlarmId}")
    public ResponseEntity<?> refuseChatRequest(@PathVariable("chatAlarmId") Long chatAlarmId){
        HttpHeaders headers=new HttpHeaders();
        headers.setLocation(URI.create("/alarm/chatRequest/"+chatServiceTemp.refuseChatRequest(chatAlarmId)));
        return new ResponseEntity<>(headers,HttpStatus.MOVED_PERMANENTLY);
    }
}
