package com.example.younet.userprofile.mypage.dto;

import com.example.younet.domain.Post;
import com.example.younet.domain.User;
import com.example.younet.post.dto.PostResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class UserProfileDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResultDTO {
        String profilePicture;
        String name;
        String hostCntr;
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
        //Long commentsCount;
    }
}
