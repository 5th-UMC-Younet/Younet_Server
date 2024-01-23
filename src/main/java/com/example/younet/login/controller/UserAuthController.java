package com.example.younet.login.controller;

import com.example.younet.login.dto.EmailVerificationDto;
import com.example.younet.login.dto.UserSignupRequestDto;
import com.example.younet.login.service.GeneralAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class UserAuthController {

    private final GeneralAuthService generalAuthService;

    // 일반 회원가입 API
    @PostMapping("/user/signup")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity signup(@Valid @RequestBody UserSignupRequestDto request) throws Exception {
        generalAuthService.signUp(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 이메일 전송 API
    @PostMapping("/signup/email")
    public ResponseEntity<String> getEmailAuthCode(@RequestParam(name = "email") String email) {
        if (generalAuthService.isDuplicatedEmail(email)) {
            generalAuthService.sendEmailAuthCode(email);
            return ResponseEntity.ok("인증 코드가 전송되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 이메일입니다.");
        }
    }

    @PostMapping("/signup/verification/email")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailVerificationDto emailVerificationDto) {
        boolean verificationResult = generalAuthService.verifyEmail(emailVerificationDto);
        if (verificationResult) {
            return ResponseEntity.ok("인증에 성공하였습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증에 실패하였습니다.");
        }
    }

}
