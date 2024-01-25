package com.example.younet.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostResponseDTO {
    //@Builder
    @Getter
    @NoArgsConstructor
    public static class postListResultDTO{
        Long postId;
        String title;
        String body;
        String categoryName;
    }
}
