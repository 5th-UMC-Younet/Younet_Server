package com.example.younet.reply.dto;


import com.example.younet.domain.Comment;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


public class ReplyResponseDTO {
    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Reply {

        public Reply(com.example.younet.domain.Reply reply) {
            this.replyId = reply.getId();
            this.commentId = reply.getCommentId();
            this.postId = reply.getPostId();
            this.communityProfileId = reply.getCommunityProfileId();
            this.authorName = reply.getCommunityProfile().getName();
            this.body = reply.getBody();
            this.createdAt = reply.getCreatedAt();
            this.updatedAt = reply.getUpdatedAt();
        }

        private final Long replyId;
        private final Long commentId;
        private final Long postId;
        private final Long communityProfileId;
        private final String authorName;
        private final String body;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

    }
}
