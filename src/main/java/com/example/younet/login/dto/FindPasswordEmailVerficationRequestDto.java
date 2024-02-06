package com.example.younet.login.dto;

import lombok.Getter;

@Getter
public class FindPasswordEmailVerficationRequestDto {
    private String loginId;
    private String code;
}
