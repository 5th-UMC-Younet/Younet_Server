package com.example.younet.chat.controller;

import com.example.younet.chat.dto.*;
import com.example.younet.chat.service.ChatService;
import com.example.younet.domain.enums.ReportReason;
import com.example.younet.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    //실명프로필 조회 API (함수화)
    @GetMapping("/{user_id}/realProfile")
    public ResponseEntity<?> getUserRealNameProfile(@PathVariable Long user_id)
    {
        return chatService.getUserRealNameProfile(user_id);
    }

    //[1:1 채팅]에서 유저 프로필 조회하는 API
    @GetMapping("/{chat_room_id}/profile")
    public ResponseEntity<?> getOneToOneUserProfile(@PathVariable Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.getOneToOneUserProfile(chat_room_id, principalDetails);
    }

    //오픈채팅방 -> [1:1 채팅] 요청
    @PostMapping("/{open_chatroom_id}/{user_id}/request")
    public ResponseEntity<?> createOpenChatRequest(@PathVariable Long open_chatroom_id, @PathVariable Long user_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.createOpenChatRequest(open_chatroom_id, user_id, principalDetails);
    }

    //오픈채팅방 상세정보 및 유저참여가능여부 GET
    @GetMapping("/{chat_room_id}/info")
    public OpenChatRoomDetailDto getOpenChatRoomInfo(@PathVariable Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.getOpenChatRoomInfo(chat_room_id, principalDetails);
    }

    //참여 버튼 눌렀을때: 오픈채팅방 입장(참여) API
    @PostMapping("/join/{chat_room_id}")
    public ResponseEntity<?> joinOpenChatRoom(@PathVariable Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.joinOpenChatRoom(chat_room_id, principalDetails);
    }

    //오픈채팅방 신규 생성
    @PostMapping("/open")
    public ResponseEntity<?> createOpenChatRoom(@RequestBody CreateOpenChatRoomDto createOpenChatRoomDto, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.createOpenChatRoom(createOpenChatRoomDto, principalDetails);
    }

    //[오픈채팅방] 참여중인 유저 목록 및 알림 여부 조회
    @GetMapping("/{chat_room_id}/users")
    public OpenChatUsersListDto getOpenChatUsersList (@PathVariable Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.getOpenChatUsersList(chat_room_id, principalDetails);
    }

    //[오픈채팅방] 유저 개별 프로필 조회 API
    @GetMapping("/{chat_room_id}/{user_id}")
    public ResponseEntity<?> getOpenUserProfile (@PathVariable Long chat_room_id, @PathVariable Long user_id)
    {
        return chatService.getOpenUserProfile(chat_room_id, user_id);
    }

    //현재 로그인된 유저가 참여중인 오픈채팅 목록
    @GetMapping("/list/open")
    public List<OpenChatListDto> readOpenChatList (@AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.readOpenChatList(principalDetails);
    }

    //신고하기 API (POST)
    @PostMapping("/report/{user_id}")
    public ResponseEntity<?> reportUser(@PathVariable Long user_id, @RequestBody ReportRequestDto reportRequestDto, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        return chatService.reportUser(user_id, reportRequestDto, principalDetails);
    }

    //전체 오픈채팅 목록 조회
    @GetMapping("/all/open")
    public List<OpenChatListDto> readAllOpenChatList()
    {
        return chatService.readAllOpenChatList();
    }


    //TODO: 오픈채팅방-개별 채팅방 내 메세지 목록 불러오기

    //TODO: 채팅방 입장(소켓 연결)

    //TODO: 채팅 메세지 전송 API


}