package com.example.younet.userprofile.profile.dto;

import io.micrometer.common.lang.Nullable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class UserProfileDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResultDTO {
        Long userId;
        Long communityProfileId;
        String profilePicture;
        String name;
        @Nullable
        String likeCntr;
        String profileText;
        List<userProfilePostDTO> posts;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class userProfilePostDTO{
        Long postId;
        Long categoryId;
        String imageSampleUrl;
        String title;
        String bodySample;
        LocalDateTime createdAt;
        Long likesCount;
        Long commentsCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileEditDTO {
        String name;
        String profileText;
        String mainSkl;
        String hostCntr;
        String hostSkl;
    }
}
