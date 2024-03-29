package com.example.younet.comment.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


public class CommentRequestDTO {
    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class GetByPostIdWithPaging {
        private final Long postId;
        private final int pageNum;
        private final int pageSize;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class GetByCommunityProfileIdWithPaging {
        private final Long communityProfileId;
        private final int pageNum;
        private final int pageSize;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Post {
        private final Long postId;
        private final Long communityProfileId;
        private final String body;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Update {
        private final Long commentId;
        private final String body;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Delete {
        private final Long commentId;
    }
}
