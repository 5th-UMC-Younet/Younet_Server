package com.example.younet.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PostRequestDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddPostDTO{
        @NotBlank
        String title;
        @NotNull
        Long communityProfileId;
        @NotNull
        Long countryId;
        @NotNull
        Long categoryId;

        List<SectionDTO> sections;
    }
}
