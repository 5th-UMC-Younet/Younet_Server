package com.example.younet.comment.controller;


import com.example.younet.comment.dto.CommentRequestDTO;
import com.example.younet.comment.dto.CommentResponseDTO;
import com.example.younet.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("post")
    public Slice<CommentResponseDTO.Comment> getCommentsByPostIdWithPaging(@RequestBody CommentRequestDTO.GetByPostIdWithPaging requestDto) {
        return commentService.getCommentSliceByPostId(requestDto);

    }

    @GetMapping("community-profile")
    public Slice<CommentResponseDTO.Comment> getCommentsByCommunityProfileIdWithPaging(@RequestBody CommentRequestDTO.GetByCommunityProfileIdWithPaging requestDto) {
        return commentService.getCommentSliceByCommunityProfileId(requestDto);
    }

    @PostMapping("")
    public Long postComment(@RequestBody CommentRequestDTO.Post requestDto) {
        return commentService.saveComment(requestDto);

    }

    @PatchMapping("")
    public CommentResponseDTO.Comment updateComment(@RequestBody CommentRequestDTO.Update requestDto) {
        return commentService.updateComment(requestDto);

    }

    @DeleteMapping("")
    public Long deleteComment(@RequestBody CommentRequestDTO.Delete requestDto) {
        return commentService.deleteComment(requestDto);
    }
}
