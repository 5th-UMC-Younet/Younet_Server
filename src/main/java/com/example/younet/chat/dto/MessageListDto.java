package com.example.younet.chat.dto;

import com.example.younet.domain.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MessageListDto{
    private Long messageId;
    private Long userId;
    private boolean isRead; //확인 여부
    private boolean isFile; //0: 텍스트메세지, 1: 파일메세지
    private String message; //메세지 내용(message)
    private LocalDateTime createdAt; // 가장 최근에 해당하는 메세지 생성 시각(created_at)

    public MessageListDto(ChatMessage chatMessage)
    {
        this.messageId = chatMessage.getId();
        this.userId = chatMessage.getUser().getId();
        this.isRead = chatMessage.isRead();
        this.isFile = chatMessage.isFile();
        this.message = chatMessage.getMessage();
        this.createdAt = chatMessage.getCreatedAt();
    }
}

