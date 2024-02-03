package com.example.younet.login.dto;

import lombok.Getter;

@Getter
public class SignupEmailVerificationDto {
    private String userEmail;
    private String code;
}