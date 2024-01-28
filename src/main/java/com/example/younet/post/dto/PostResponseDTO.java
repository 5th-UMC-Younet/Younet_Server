package com.example.younet.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class postListResultDTO{
        Long postId;
        String title;
        String categoryName;
        Long likesCount;
    }
}
