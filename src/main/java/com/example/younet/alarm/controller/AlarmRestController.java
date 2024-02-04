package com.example.younet.alarm.controller;

import com.example.younet.ApiPayload.ApiResponse;
import com.example.younet.alarm.service.AlarmCommandService;
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
@RequestMapping("/alarm")
public class AlarmRestController {
    private final AlarmCommandService alarmCommandService;
    @GetMapping("/chatRequest")
    public void getListOfChatRequestAlarm(){

    }
    // 알림 확인
    @GetMapping("/confirm/{alarmId}")
    public ResponseEntity<?> confirmCommonAlarm(@PathVariable("alarmId") Long alarmId){
        HttpHeaders headers=new HttpHeaders();
        long postId=alarmCommandService.updateIsConfirmed(alarmId);
        headers.setLocation(URI.create("/post/"+postId));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
    @GetMapping("/delete/{alarmId}")
    public ApiResponse<?> deleteCommonAlarm(@PathVariable("alarmId") Long alarmId){
        long postId=alarmCommandService.updateIsConfirmed(alarmId);
        return ApiResponse.onSuccess(HttpStatus.OK,postId);
    }
}