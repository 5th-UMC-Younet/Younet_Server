package com.example.younet.like.dto;

import com.example.younet.domain.PostLikes;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class LikeResponseDTO {

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class CountPostLikes {
        private final Long postId;
        private final Long likeCount;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Add {
        public Add(PostLikes postLike) {
            this.id = postLike.getId();
            this.postId = postLike.getPost().getId();
            this.communityProfileId = postLike.getCommunityProfile().getId();
        }

        private final Long id;
        private final Long postId;
        private final Long communityProfileId;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Remove {
        public Remove(PostLikes postLike) {
            this.id = postLike.getId();
            this.postId = postLike.getPost().getId();
            this.communityProfileId = postLike.getCommunityProfile().getId();
        }
        private final Long id;
        private final Long postId;
        private final Long communityProfileId;
    }
}
