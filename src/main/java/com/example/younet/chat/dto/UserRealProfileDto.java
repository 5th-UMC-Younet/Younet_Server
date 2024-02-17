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
public class UserRealProfileDto { //실명 프로필 조회용 DTO
    private Long userId; //유저 id
    private String name; //채팅에 참여하는 상대방 실명
    private String profilePicture; //채팅에 참여하는 상대방 프로필 이미지
    private String mainSkl; //본교
    private String hostContr; //유학국
    private String hostSkl; //파견교
    private String profileText; //프로필 소개글
}

