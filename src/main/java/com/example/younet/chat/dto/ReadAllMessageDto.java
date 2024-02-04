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
public class ReadAllMessageDto{
    Long loginUserId; //현재 로그인된 사용자 id
    List<MessageListDto> messageListDtoList; //메세지 리스트
}

