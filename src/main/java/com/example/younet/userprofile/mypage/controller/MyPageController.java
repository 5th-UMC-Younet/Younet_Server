package com.example.younet.userprofile.mypage.controller;

import com.example.younet.global.dto.ApplicationResponse;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.userprofile.mypage.dto.MyPageDto;
import com.example.younet.userprofile.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    // 마이페이지 조회
    @GetMapping("/mypage/info")
    public ApplicationResponse<MyPageDto.MyProfileDTO> mypageInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        MyPageDto.MyProfileDTO myPageDto = myPageService.getMyPageInfo(principalDetails);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, myPageDto);
    }

    // 마이페이지 수정 조회
    @GetMapping("/mypage/edit")
    public ApplicationResponse<MyPageDto.MyProfileInfoDTO> myPageInfoEdit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        MyPageDto.MyProfileInfoDTO myProfileInfoDTO = myPageService.getMyPageInfoEdit(principalDetails);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, myProfileInfoDTO);
    }

    // 마이페이지 수정
    @PatchMapping("/mypage/edit")
    public ResponseEntity<String> editMyPageInfo(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestPart("editMypage") MyPageDto.MyProfileEditDTO myProfileEditDTO,
                                                 @RequestPart("file")MultipartFile file) {
        myPageService.patchEditMyPageInfo(principalDetails, myProfileEditDTO, file);
        return ResponseEntity.ok("프로필이 수정되었습니다.");
    }
}
