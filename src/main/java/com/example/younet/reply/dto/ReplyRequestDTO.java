package com.example.younet.reply.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


public class ReplyRequestDTO {
    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class GetByCommentIdWithPaging {
        private final Long commentId;
        private final int pageNum;
        private final int pageSize;
    }

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
        private final Long commentId;
        private final Long postId;
        private final Long communityProfileId;
        private final String body;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Update {
        private final Long replyId;
        private final String body;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Delete {
        private final Long replyId;
    }
}
