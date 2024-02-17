package com.example.younet.chat.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CreateOpenChatRoomDto {
    private String profile; //닉네임/실명 채팅방 여부

    private String thumbnail; //썸네일 이미지
    private String title; //제목
    private String description; //채팅방 설명

    private String mainSkl; //본교
    private String hostContr; //유학국
    private String hostSkl; //파견교

}

