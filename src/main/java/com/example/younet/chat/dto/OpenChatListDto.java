package com.example.younet.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class OpenChatListDto{
    private Long chatRoomId; //오픈채팅방 id
    private String title; //오픈채팅방 이름
    private String thumbnail; //오픈채팅방 대표이미지
    private String message; //가장 최근 메세지 내용(message)
    private LocalDateTime createdAt; // 가장 최근에 해당하는 메세지 생성 시각(created_at)
    private int participants; //참여자수
}

