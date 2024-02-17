package com.example.younet.userprofile.mypage.dto;

import com.example.younet.userprofile.profile.dto.UserProfileDto;
import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MyPageDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProfileDTO {
        Long userId;
        String profilePicture;
        String name;
        @Nullable
        String likeCntr;
        String profileText;
        List<MyProfilePostDTO> posts;
        List<MyProfilePostDTO> scraps;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProfileInfoDTO {
        String profilePicture;
        String name;
        String nickname;
        @Nullable
        String likeCntr;
        String profileText;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProfileEditDTO {
        String name;
        String nickname;
        @Nullable
        String likeCntr;
        String profileText;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProfilePostDTO{
        Long postId;
        Long categoryId;
        String imageSampleUrl;
        String title;
        String bodySample;
        LocalDateTime createdAt;
        Long likesCount;
        Long commentsCount;
    }
}
