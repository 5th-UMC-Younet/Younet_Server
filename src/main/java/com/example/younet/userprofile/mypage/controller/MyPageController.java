package com.example.younet.userprofile.mypage.controller;

import com.example.younet.global.dto.ApplicationResponse;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.userprofile.mypage.dto.MyPageDto;
import com.example.younet.userprofile.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    // 커뮤니티 -> 유저 프로필 조회
    @GetMapping("/mypage/info")
    public ApplicationResponse<MyPageDto.MyProfileDTO> getMypageInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        MyPageDto.MyProfileDTO myPageDto = myPageService.myPageInfo(principalDetails);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, myPageDto);
    }

    // 커뮤니티 -> 유저 프로필 수정 페이지 조회
}
