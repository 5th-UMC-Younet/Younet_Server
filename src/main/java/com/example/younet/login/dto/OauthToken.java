package com.example.younet.login.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OauthToken implements Serializable {

    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int refresh_token_expires_in;

}
