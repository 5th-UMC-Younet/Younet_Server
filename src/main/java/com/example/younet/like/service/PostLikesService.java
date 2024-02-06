package com.example.younet.like.service;


import com.example.younet.alarm.repository.CommonAlarmRepository;
import com.example.younet.domain.*;
import com.example.younet.domain.enums.AlarmType;
import com.example.younet.like.dto.LikeRequestDTO;
import com.example.younet.like.repository.PostLikesRepository;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostLikesService {

    private final PostLikesRepository postLikesRepository;
    private final PostRepository postRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final CommonAlarmRepository commonAlarmRepository;

    @Transactional
    public PostLikes addLike(LikeRequestDTO.Add requestDto) {
        Post targetPost = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post ID를 찾을 수 없습니다."));
        CommunityProfile targetCommuProfile = communityProfileRepository.findById(requestDto.getCommunityProfileId())
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티 프로필 ID를 찾을 수 없습니다."));

        Optional<PostLikes> postLikesOptional = postLikesRepository
                .findByPost_IdAndCommunityProfile_Id(targetPost.getId(),targetCommuProfile.getId());

        targetPost.addLike();

        if(postLikesOptional.isEmpty()) { // 어떤 유저가 특정 게시글에 처음으로 좋아요를 누르는 경우
            PostLikes savedLike = postLikesRepository.save(PostLikes.builder()
                    .post(targetPost)
                    .communityProfile(targetCommuProfile)
                    .build());

            CommonAlarm commonAlarm=CommonAlarm.builder()
                    .alarmType(AlarmType.LIKE)
                    .isConfirmed(false)
                    .postId(targetPost.getId())
                    .receiver(targetPost.getCommunityProfile())
                    .actorName(targetCommuProfile.getName())
                    .build();
            commonAlarmRepository.save(commonAlarm);
            return savedLike;
        }

        PostLikes targetPostLike = postLikesOptional.get();
        targetPostLike.activateLike();
        return  targetPostLike;
    }

    @Transactional
    public Long countLike(LikeRequestDTO.CountPostLikes requestDto) {
        Post targetPost = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post ID를 찾을 수 없습니다."));

        return targetPost.getLikesCount();
    }


    @Transactional
    public PostLikes removeLike(LikeRequestDTO.Remove requestDto) {
        PostLikes target = postLikesRepository.findByPost_IdAndCommunityProfile_Id(requestDto.getPostId(), requestDto.getCommunityProfileId())
                .orElseThrow(() -> new IllegalArgumentException("Post ID, 혹은 커뮤니티 프로필 ID를 찾을 수 없습니다."));

        Post targetPost = target.getPost();

        target.deactivateLike();
        targetPost.removeLike();
        return target;
    }
}
