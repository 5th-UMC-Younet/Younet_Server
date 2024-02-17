package com.example.younet.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class OpenChatUsersListDto {
    Long representerId; //채팅방 방장 id
    Long loginUserId; //현재 로그인된 유저 id
    List<userListDTO> userListDTOList; //유저 목록
    boolean isNoti; //(현재 로그인된 유저의) 채팅방 알림 설정 여부

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class userListDTO{
        private Long userId; //유저 id

        private String thumbnail; //썸네일 이미지
        private String name; //[커뮤니티 프로필: 닉네임 / 실명프로필: 실명]
    }
}

