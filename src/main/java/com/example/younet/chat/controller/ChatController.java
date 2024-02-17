package com.example.younet.chat.controller;

import com.example.younet.chat.dto.*;
import com.example.younet.chat.service.ChatService;
import com.example.younet.domain.enums.ReportReason;
import com.example.younet.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    //커뮤니티 프로필 -> [1:1 채팅] 요청하는 경우
    @PostMapping("/community/{user_id}")
    public ResponseEntity<?> createNicknameChatRequest(@PathVariable Long user_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.createNicknameChatRequest(user_id, principalDetails);
    }

    //참여중인 1:1 채팅 목록
    @GetMapping("/list/oneToOne")
    public List<OneToOneChatListDto> readOneToOneChatList(@AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.readOneToOneChatList(principalDetails);
    }

    // 1:1 채팅방 메세지 불러오기 (개별 채팅방에 대한 모든 메세지 리스트 GET)
    @GetMapping("/{chat_room_id}/message")
    public ReadAllMessageDto readAllMessages(@PathVariable Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.readAllMessages(chat_room_id, principalDetails);
    }

    //[1:1 채팅] 요청 수락 + 채팅방 생성 API
    @PostMapping("/accept/{chatAlarmId}")
    public ResponseEntity<?> acceptChatRequest(@PathVariable("chatAlarmId") Long chatAlarmId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        return chatService.acceptChatRequest(chatAlarmId, principalDetails);
    }

    //[1:1 채팅]에서 유저 프로필 조회하는 API
    @GetMapping("/{chat_room_id}/profile")
    public ResponseEntity<?> getOneToOneUserProfile(@PathVariable Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.getOneToOneUserProfile(chat_room_id, principalDetails);
    }

}