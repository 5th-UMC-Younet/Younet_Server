package com.example.younet.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MessageDto{
//    private boolean isFile; //0: 텍스트메세지, 1: 파일메세지
    private String message; //메세지 내용(message)
}

