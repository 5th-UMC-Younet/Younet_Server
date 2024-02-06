package com.example.younet.profileauth.controller;

import com.example.younet.global.dto.ApplicationResponse;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.profileauth.dto.ProfileAuthRequestDto;
import com.example.younet.profileauth.service.ProfileAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileAuthController {

    private final ProfileAuthService profileAuthService;

    // 본인 인증 여부 확인
    @GetMapping("/profile/auth")
    public ApplicationResponse<Integer> isProfileAuth(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, profileAuthService.getIsProfileAuth(principalDetails));
    }

    // 본인 인증 요청
    @PostMapping("/profile/auth")
    public ApplicationResponse<String> profileAuth(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody ProfileAuthRequestDto profileAuthRequestDto) {
        profileAuthService.requestProfileAuth(principalDetails, profileAuthRequestDto);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "파일이 정상적으로 제출되었습니다.");
    }

}