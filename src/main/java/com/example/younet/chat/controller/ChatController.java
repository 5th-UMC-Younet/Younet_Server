package com.example.younet.chat.controller;

import com.example.younet.chat.dto.OneToOneChatListDto;
import com.example.younet.chat.service.ChatService;
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
    @PostMapping("/{user_id}")
    public ResponseEntity<?> createNicknameChatRequest(@PathVariable Long user_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.createNicknameChatRequest(user_id, principalDetails);
    }

    //참여중인 1:1 채팅 목록
    @GetMapping("/list")
    public List<OneToOneChatListDto> readOneToOneChatList(@AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.readOneToOneChatList(principalDetails);
    }

    //참여중인 오픈채팅 목록

    //오픈채팅방 생성

    //오픈채팅방 -> 참여 정보 (JoinChat 테이블)

    //오픈채팅방 -> 참여중인 유저 목록

    //오픈채팅방 -> 사용자 프로필 조회 (실명채팅방, 닉네임채팅방)

    //오픈채팅방 -> [1:1 채팅] 요청
//    @PostMapping("/{user_id}")
//    public ResponseEntity<?> createOpenChatRequest(@PathVariable Long user_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
//    {
//        return chatService.createOpenChatRequest(user_id, principalDetails);
//    }

    //오픈채팅방 나가기 (JoinChat 테이블에서 삭제)

}