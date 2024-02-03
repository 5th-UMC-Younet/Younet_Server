package com.example.younet.comment.service;

import com.example.younet.alarm.repository.CommonAlarmRepository;
import com.example.younet.comment.dto.CommentRequestDTO;
import com.example.younet.comment.dto.CommentResponseDTO;
import com.example.younet.comment.repository.CommentRepository;
import com.example.younet.domain.Comment;
import com.example.younet.domain.CommonAlarm;
import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.Post;
import com.example.younet.domain.enums.AlarmType;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final CommonAlarmRepository commonAlarmRepository;

    @Transactional
    public Slice<CommentResponseDTO.Comment> getCommentSliceByPostId(CommentRequestDTO.GetByPostIdWithPaging requestDto) {
        PageRequest pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), Sort.by(Sort.Direction.ASC, "createdAt"));

        return commentRepository.findSliceByPost_Id(requestDto.getPostId(), pageable)
                .map(CommentResponseDTO.Comment::new);
    }

    @Transactional
    public Slice<CommentResponseDTO.Comment> getCommentSliceByCommunityProfileId(CommentRequestDTO.GetByCommunityProfileIdWithPaging requestDto) {
        PageRequest pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), Sort.by(Sort.Direction.ASC, "createdAt"));

        return commentRepository.findSliceByCommunityProfile_id(requestDto.getCommunityProfileId(), pageable)
                .map(CommentResponseDTO.Comment::new);
    }

    @Transactional
    public Long saveComment(CommentRequestDTO.Post requestDto) {

        CommunityProfile communityProfile = communityProfileRepository.findById(requestDto.getCommunityProfileId())
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티 프로필을 찾을 수 없습니다."));
        Post post = postRepository.findById(requestDto.getCommunityProfileId())
                .orElseThrow(() -> new IllegalArgumentException("댓글을 달 게시글 ID를 찾을 수 없습니다."));

        Comment newComment = Comment.builder()
                .post(post)
                .communityProfile(communityProfile)
                .body(requestDto.getBody())
                .build();

        CommonAlarm commentAlarm = CommonAlarm.builder()
                .alarmType(AlarmType.COMMENT)
                .isConfirmed(false)
                .postId(post.getId())
                .receiver(post.getCommunityProfile())
                .actorId(communityProfile.getId())
                .build();
        commonAlarmRepository.save(commentAlarm);

        commentRepository.save(newComment);
        return newComment.getId();
    }

    @Transactional
    public CommentResponseDTO.Comment updateComment(CommentRequestDTO.Update requestDto) {
        Comment comment = commentRepository.findById(requestDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("댓글 ID를 찾을 수 없습니다."));

        comment.updateComment(requestDto);
        return new CommentResponseDTO.Comment(comment);
    }

    @Transactional
    public Long deleteComment(CommentRequestDTO.Delete requestDto) {
        commentRepository.deleteById(requestDto.getCommentId());
        return requestDto.getCommentId();
    }

}
