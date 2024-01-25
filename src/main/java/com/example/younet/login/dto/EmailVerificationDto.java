package com.example.younet.login.dto;

import lombok.Getter;

@Getter
public class EmailVerificationDto {
    private String userEmail;
    private String code;
}