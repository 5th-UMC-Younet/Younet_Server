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
public class OpenChatRoomDetailDto {
    private Long openChatRoomId; //채팅방 id
    private String profile; //닉네임/실명 채팅방 여부
    //private Long participate; //TODO: 참여중인 인원

    private String thumbnail; //썸네일 이미지
    private String title; //제목
    private String description; //채팅방 설명

    private String mainSkl; //본교
    private String hostContr; //유학국
    private String hostSkl; //파견교

    private Long userId; //로그인된 유저 ID
    private boolean isJoin; //참여 가능 여부
}

