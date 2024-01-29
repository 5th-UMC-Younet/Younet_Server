package com.example.younet.post.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ImageResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonPropertyOrder({"imageId","imageUrl"})
    public static class ImageResultDTO{
        Long ImageId;
        String imageUrl;
    }
}
