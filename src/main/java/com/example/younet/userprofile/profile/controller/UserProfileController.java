package com.example.younet.userprofile.profile.controller;

import com.example.younet.global.dto.ApplicationResponse;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.userprofile.profile.dto.UserProfileDto;
import com.example.younet.userprofile.profile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // 커뮤니티 -> 유저 프로필 조회
    @GetMapping("/user/profile/{userId}")
    public ApplicationResponse<UserProfileDto.UserResultDTO> getUserProfileInfo(@PathVariable Long userId) {
        UserProfileDto.UserResultDTO userProfileDto = userProfileService.findUserInfo(userId);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, userProfileDto);
    }
}
