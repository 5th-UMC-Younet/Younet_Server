package com.example.younet.chat.dto;

import com.example.younet.domain.OpenMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OpenMessageListDto{
    private Long messageId;
    private Long userId;
    private boolean isFile; //0: 텍스트메세지, 1: 파일메세지
    private String message; //메세지 내용(message)
    private LocalDateTime createdAt; // 가장 최근에 해당하는 메세지 생성 시각(created_at)

    public OpenMessageListDto(OpenMessage openMessage)
    {
        this.messageId = openMessage.getId();
        this.userId = openMessage.getUser().getId();
        this.isFile = openMessage.isFile();
        this.message = openMessage.getMessage();
        this.createdAt = openMessage.getCreatedAt();
    }
}

