package com.example.younet.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class OpenChatSearchListDto{
    private Long chatRoomId; //오픈채팅방 id
    private String title; //오픈채팅방 이름
    private String thumbnail; //오픈채팅방 대표이미지
    private String description; //오픈채팅방 설명
    private int participants; //참여자수
}

