package com.example.younet.global.errorException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{
    public ErrorCode errorCode;
}
