package com.example.younet.comment.service;

import com.example.younet.comment.dto.CommentRequestDTO;
import com.example.younet.comment.dto.CommentResponseDTO;
import com.example.younet.comment.repository.CommentRepository;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public Slice<CommentResponseDTO.Comment> getCommentSliceByPostId(CommentRequestDTO.GetByPostIdWithPaging requestDto) {
        PageRequest pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), Sort.by(Sort.Direction.ASC, "created_at"));

        return commentRepository.findSliceByPost_Id(requestDto.getPostId(), pageable)
                .map(CommentResponseDTO.Comment::new);
    }

    public Slice<CommentResponseDTO.Comment> getCommentSliceByCommunityProfileId(CommentRequestDTO.GetByCommunityProfileIdWithPaging requestDto) {
        PageRequest pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), Sort.by(Sort.Direction.ASC, "created_at"));

        return commentRepository.findSliceByCommunityProfile_id(requestDto.getCommunityProfileId(), pageable)
                .map(CommentResponseDTO.Comment::new);
    }
}
