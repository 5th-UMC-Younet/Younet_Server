package com.example.younet.login.controller;

import com.example.younet.login.dto.UserSignupRequestDto;
import com.example.younet.login.service.AuthService;
import com.example.younet.login.service.GeneralAuthService;
import com.example.younet.login.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAuth {

    private final UserService userService;
    private final GeneralAuthService generalAuthService;

    // 일반 회원가입
    @PostMapping("/user/signup")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity signup(@Valid @RequestBody UserSignupRequestDto request) throws Exception {
        generalAuthService.signUp(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
