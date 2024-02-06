package com.example.younet.login.dto;

import lombok.Data;

@Data
public class NewPasswordRequestDto {
    private String loginId;
    private String newPassword;
}
