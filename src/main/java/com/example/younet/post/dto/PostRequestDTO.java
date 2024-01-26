package com.example.younet.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public class PostRequestDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDTO{
        @NotBlank
        String title;
        @NotBlank
        String body;
        @NotNull
        Long communityProfileId;
        @NotNull
        Long countryId;
        @NotNull
        Long categoryId;

        MultipartFile postImage;
    }
}
