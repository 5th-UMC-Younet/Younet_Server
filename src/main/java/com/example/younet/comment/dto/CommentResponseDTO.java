package com.example.younet.comment.dto;


import com.example.younet.domain.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


public class CommentResponseDTO {
    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Comment {

        public Comment(com.example.younet.domain.Comment comment) {
            this.commentId = comment.getId();
            this.postId = comment.getPostId();
            this.communityProfileId = comment.getCommunityProfileId();
            this.body = comment.getBody();
            this.createdAt = comment.getCreatedAt();
            this.updatedAt = comment.getUpdatedAt();
        }

        private final Long commentId;
        private final Long postId;
        private final Long communityProfileId;
        private final String body;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
    }
}
