package com.example.younet.post.dto;

import lombok.Builder;
import lombok.Getter;

public class PostResponseDTO {
    @Builder
    @Getter
    public static class postListResultDTO{
        Long postId;
        String title;
        String body;
        String categoryName;
    }
}
