package com.example.younet.like.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class LikeRequestDTO {

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
    public static class CountPostLikes {
        private final Long postId;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Remove {
        private final Long postId;
        private final Long communityProfileId;
    }
}
