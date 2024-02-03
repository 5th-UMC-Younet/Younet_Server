package com.example.younet.userprofile.profile.dto;

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
        String profilePicture;
        String name;
        String likeCntr;
        String profileText;
        List<userProfilePostDTO> posts;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class userProfilePostDTO{
        String imageSampleUrl;
        String title;
        String bodySample;
        LocalDateTime createdAt;
        Long likesCount;
        Long commentsCount;
    }
}
