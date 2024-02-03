package com.example.younet.userprofile.mypage.controller;

import com.example.younet.global.dto.ApplicationResponse;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.userprofile.mypage.dto.UserProfileDto;
import com.example.younet.userprofile.mypage.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/user/profile/{userId}")
    public ApplicationResponse<UserProfileDto.UserResultDTO> getMypageInfo(@PathVariable Long userId) {
        UserProfileDto.UserResultDTO userProfileDto = userProfileService.findUserInfo(userId);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, userProfileDto);
    }
}
