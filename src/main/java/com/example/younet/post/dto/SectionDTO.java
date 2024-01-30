package com.example.younet.post.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    @JsonPropertyOrder({"sectionId","body","images"})
    public static class SectionResultDTO{
        Long SectionId;
        String body;
        List<ImageResponseDTO.ImageResultDTO> images;
    }
}
