package com.example.younet.login.controller;

import com.example.younet.global.dto.ApplicationResponse;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.JwtTokenDto;
import com.example.younet.login.dto.EmailVerificationDto;
import com.example.younet.login.dto.ReissueRequestDto;
import com.example.younet.login.dto.UserSigninRequestDto;
import com.example.younet.login.dto.UserSignupRequestDto;
import com.example.younet.login.service.AuthService;
import com.example.younet.login.service.GeneralAuthService;
import com.example.younet.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;
    private final GeneralAuthService generalAuthService;
    private final AuthService authService;

    // 일반 회원가입
    @PostMapping("/user/signup")
    public ApplicationResponse<String> generalSignUp(@RequestBody UserSignupRequestDto requestDto) {
        // 코드 리팩토링 필수(URI 설정, 비밀번호 암호화 등)
        System.out.println("일반 회원가입");
        if(generalAuthService.isDuplicatedEmail(requestDto.getEmail())) {
            generalAuthService.signUp(requestDto);
        }
        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, "회원가입 성공");
    }

    // 이메일 인증 코드 전송
    @PostMapping("/signup/email")
    public ResponseEntity<String> getEmailAuthCode(@RequestParam(name = "email") String email) {
        if (generalAuthService.isDuplicatedEmail(email)) {
            generalAuthService.sendEmailAuthCode(email);
            return ResponseEntity.ok("인증 코드가 전송되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 이메일입니다.");
        }
    }

    // 이메일 인증 성공 여부 확인
    @PostMapping("/signup/verification/email")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailVerificationDto emailVerificationDto) {
        boolean verificationResult = generalAuthService.verifyEmail(emailVerificationDto);
        if (verificationResult) {
            return ResponseEntity.ok("인증에 성공하였습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증에 실패하였습니다.");
        }
    }

    // 일반 로그인
    @PostMapping("/user/login")
    public ApplicationResponse<JwtTokenDto> generalSignIn(@RequestBody UserSigninRequestDto requestDto) {
        System.out.println("일반 로그인");
        JwtTokenDto jwtTokenDto = generalAuthService.signInAndGetToken(requestDto);
        System.out.println(jwtTokenDto);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    @PostMapping("/auth/reissue")
    public ApplicationResponse<JwtTokenDto> reissue(@RequestBody ReissueRequestDto reissueRequestDto) {
        JwtTokenDto jwtTokenDto = authService.reissue(reissueRequestDto.getRefreshToken());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }
}
