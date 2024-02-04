package com.example.younet.alarm.controller;

import com.example.younet.ApiPayload.ApiResponse;
import com.example.younet.alarm.dto.AlarmResponseDTO;
import com.example.younet.alarm.service.AlarmCommandService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class AlarmRestController {
    private final AlarmCommandService alarmCommandService;
    @GetMapping("/chatRequest")
    public void getListOfChatRequestAlarm(){

    }

    @GetMapping("/community/{receiverId}")
    public Slice<AlarmResponseDTO.commonAlarmListResultDTO> getCommonAlarmList
            (@PathVariable("receiverId")Long receiverId, @Nullable @RequestParam("lastAlarm")Long lastAlarmId){
        return alarmCommandService.getCommonAlarmList(lastAlarmId,receiverId);
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