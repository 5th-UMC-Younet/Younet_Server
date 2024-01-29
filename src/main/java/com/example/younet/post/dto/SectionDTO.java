package com.example.younet.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {
    @NotBlank
    String body;
    List<String> imageKeys;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionResultDTO{
        Long id;
        String body;
        List<ImageResponseDTO.ImageResultDTO> images;
    }
}
