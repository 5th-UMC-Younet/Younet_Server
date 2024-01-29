package com.example.younet.reply.controller;


import com.example.younet.comment.dto.CommentRequestDTO;
import com.example.younet.comment.dto.CommentResponseDTO;
import com.example.younet.comment.service.CommentService;
import com.example.younet.reply.dto.ReplyRequestDTO;
import com.example.younet.reply.dto.ReplyResponseDTO;
import com.example.younet.reply.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reply")
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping("/comment")
    public Slice<ReplyResponseDTO.Reply> getCommentsByPostIdWithPaging(@RequestParam ReplyRequestDTO.GetByCommentIdWithPaging requestDto) {
        return replyService.getReplySliceByCommentId(requestDto);

    }

    @GetMapping("/post")
    public Slice<ReplyResponseDTO.Reply> getCommentsByPostIdWithPaging(@RequestParam ReplyRequestDTO.GetByPostIdWithPaging requestDto) {
        return replyService.getReplySliceByPostId(requestDto);

    }

    @GetMapping("/community-profile")
    public Slice<ReplyResponseDTO.Reply> getCommentsByCommunityProfileIdWithPaging(@RequestParam ReplyRequestDTO.GetByCommunityProfileIdWithPaging requestDto) {
        return replyService.getReplySliceByCommunityProfileId(requestDto);
    }

    @PostMapping("/")
    public Long postComment(@RequestParam ReplyRequestDTO.Post requestDto) {
        return replyService.saveReply(requestDto);

    }

    @PatchMapping("/")
    public ReplyResponseDTO.Reply postComment(@RequestParam ReplyRequestDTO.Update requestDto) {
        return replyService.updateReply(requestDto);

    }

    @DeleteMapping("/")
    public Long deleteComment(@RequestParam ReplyRequestDTO.Delete requestDto) {
        return replyService.deleteComment(requestDto);
    }
}
