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
public class OneToOneChatListDto{
    private Long chatRoomId; //채팅방 id
    private String name; //채팅에 참여하는 상대방 닉네임/실명
    private String profilePicture; //채팅에 참여하는 상대방 프로필 이미지
    private String message; //가장 최근 메세지 내용(message)
    private LocalDateTime createdAt; // 가장 최근에 해당하는 메세지 생성 시각(created_at)
    private boolean profile; // 익명/실명 채팅 여부 (0: 익명(닉네임), 1: 실명)
}

