package com.example.younet.reply.service;

import com.example.younet.alarm.repository.CommonAlarmRepository;
import com.example.younet.comment.dto.CommentRequestDTO;
import com.example.younet.comment.dto.CommentResponseDTO;
import com.example.younet.comment.repository.CommentRepository;
import com.example.younet.domain.Comment;
import com.example.younet.domain.CommonAlarm;
import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.Post;
import com.example.younet.domain.Reply;
import com.example.younet.domain.enums.AlarmType;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.reply.dto.ReplyRequestDTO;
import com.example.younet.reply.dto.ReplyResponseDTO;
import com.example.younet.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final CommonAlarmRepository commonAlarmRepository;

    @Transactional
    public Slice<ReplyResponseDTO.Reply> getReplySliceByCommentId(ReplyRequestDTO.GetByCommentIdWithPaging requestDto) {
        PageRequest pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), Sort.by(
                Sort.Order.asc("comment.createdAt"),
                Sort.Order.asc("createdAt")));

        return replyRepository.findSliceByPost_Id(requestDto.getCommentId(), pageable)
                .map(ReplyResponseDTO.Reply::new);
    }

    @Transactional
    public Slice<ReplyResponseDTO.Reply> getReplySliceByPostId(ReplyRequestDTO.GetByPostIdWithPaging requestDto) {
        PageRequest pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), Sort.by(
                Sort.Order.asc("comment.createdAt"),
                Sort.Order.asc("createdAt")));

        return replyRepository.findSliceByPost_Id(requestDto.getPostId(), pageable)
                .map(ReplyResponseDTO.Reply::new);
    }

    @Transactional
    public Slice<ReplyResponseDTO.Reply> getReplySliceByCommunityProfileId(ReplyRequestDTO.GetByCommunityProfileIdWithPaging requestDto) {
        PageRequest pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), Sort.by(
                Sort.Order.asc("comment.createdAt"),
                Sort.Order.asc("createdAt")));

        return replyRepository.findSliceByCommunityProfile_id(requestDto.getCommunityProfileId(), pageable)
                .map(ReplyResponseDTO.Reply::new);
    }

    @Transactional
    public Long saveReply(ReplyRequestDTO.Post requestDto) {

        CommunityProfile communityProfile = communityProfileRepository.findById(requestDto.getCommunityProfileId())
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티 프로필을 찾을 수 없습니다."));
        Post post = postRepository.findById(requestDto.getCommunityProfileId())
                .orElseThrow(() -> new IllegalArgumentException("댓글을 달 게시글 ID를 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(requestDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("답글을 달 댓글 ID를 찾을 수 없습니다."));

        Reply newReply = Reply.builder()
                .comment(comment)
                .post(post)
                .communityProfile(communityProfile)
                .body(requestDto.getBody())
                .build();

        CommonAlarm replyAlarm = CommonAlarm.builder()
                .alarmType(AlarmType.REPLY)
                .isConfirmed(false)
                .postId(comment.getId())
                .receiver(comment.getCommunityProfile())
                .actorId(communityProfile.getId())
                .build();
        commonAlarmRepository.save(replyAlarm);

        replyRepository.save(newReply);
        return newReply.getId();
    }

    @Transactional
    public ReplyResponseDTO.Reply updateReply(ReplyRequestDTO.Update requestDto) {
        Reply reply = replyRepository.findById(requestDto.getReplyId())
                .orElseThrow(() -> new IllegalArgumentException("답글 ID를 찾을 수 없습니다."));

        reply.updateReply(requestDto);
        return new ReplyResponseDTO.Reply(reply);
    }

    @Transactional
    public Long deleteComment(ReplyRequestDTO.Delete requestDto) {
        replyRepository.deleteById(requestDto.getReplyId());
        return requestDto.getReplyId();
    }

}
