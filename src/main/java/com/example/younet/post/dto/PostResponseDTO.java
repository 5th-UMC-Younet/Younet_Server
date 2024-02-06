package com.example.younet.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class postListResultDTO{
        Long postId;
        String title;
        String bodySample;
        String imageSampleUrl;
        String categoryName;
        Long likesCount;
        LocalDateTime createdAt;
        Long commentsCount;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class searchPostResultDTO{
        String categoryName;
        List<postListResultDTO> postListResultDTOS;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddPostResultDTO{
        Long postId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedPostResultDTO{
        long postId;
        String authorName;
        String postTitle;
        Long likesCount;
        List<SectionDTO.SectionResultDTO> sections;
        // 작성자 이름
        // 날짜
        // commentsCount
        LocalDateTime createdAt;
        Long commentsCount;
    }
}
