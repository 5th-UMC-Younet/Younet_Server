package com.example.younet.scrap.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ScrapRequestDTO {

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Add {
        private final Long postId;
        private final Long communityProfileId;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class GetScrappedPosts {
        private final Long communityProfileId;
        private final int pageNum;
        private final int pageSize;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Remove {
        private final Long postId;
        private final Long communityProfileId;
    }
}
