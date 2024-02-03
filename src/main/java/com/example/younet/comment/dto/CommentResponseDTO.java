package com.example.younet.comment.dto;


import com.example.younet.domain.Comment;
import com.example.younet.domain.Reply;
import com.example.younet.reply.dto.ReplyResponseDTO;
import java.util.List;
import java.util.stream.Collectors;
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
            this.replyList = comment.getReplyList().stream()
                    .map(element -> new ReplyResponseDTO.Reply(element))
                    .collect(Collectors.toList());
            this.createdAt = comment.getCreatedAt();
            this.updatedAt = comment.getUpdatedAt();
        }

        private final Long commentId;
        private final Long postId;
        private final Long communityProfileId;
        private final String body;
        private final List<ReplyResponseDTO.Reply> replyList;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
    }
}
