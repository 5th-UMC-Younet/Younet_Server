package com.example.younet.userprofile.profile.controller;

import com.example.younet.global.dto.ApplicationResponse;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.userprofile.mypage.dto.MyPageDto;
import com.example.younet.userprofile.profile.dto.UserProfileDto;
import com.example.younet.userprofile.profile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // 커뮤니티 프로필 조회
    @GetMapping("/user/profile/{userId}")
    public ApplicationResponse<UserProfileDto.UserResultDTO> userProfileInfo(@PathVariable Long userId) {
        UserProfileDto.UserResultDTO userProfileDto = userProfileService.getUserProfileInfo(userId);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, userProfileDto);
    }

    // 실명프로필 수정
    @PatchMapping("/user/profile/edit")
    public ResponseEntity<String> editUserProfileInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestPart("editProfile") UserProfileDto.UserProfileEditDTO userProfileEditDTO,
                                                 @RequestPart("file") MultipartFile file) {
        userProfileService.patchEditUserProfileInfo(principalDetails, userProfileEditDTO, file);
        return ResponseEntity.ok("실명 프로필이 수정되었습니다.");
    }
}
