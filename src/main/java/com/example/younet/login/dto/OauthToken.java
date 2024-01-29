package com.example.younet.login.dto;

import lombok.Getter;

@Getter
public class OauthToken {
    private Integer code;
    private String msg;
    private Long id;
    private Integer expires_in;
    private Integer app_id;
}
