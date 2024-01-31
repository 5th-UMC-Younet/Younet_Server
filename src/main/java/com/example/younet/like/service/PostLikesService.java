package com.example.younet.like.service;


import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.Post;
import com.example.younet.domain.PostLikes;
import com.example.younet.like.dto.LikeRequestDTO;
import com.example.younet.like.repository.PostLikesRepository;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostLikesService {

    private final PostLikesRepository postLikesRepository;
    private final PostRepository postRepository;
    private final CommunityProfileRepository communityProfileRepository;

    @Transactional
    public PostLikes addLike(LikeRequestDTO.Add requestDto) {
        Post targetPost = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post ID를 찾을 수 없습니다."));
        CommunityProfile targetCommuProfile = communityProfileRepository.findById(requestDto.getCommunityProfileId())
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티 프로필 ID를 찾을 수 없습니다."));

        PostLikes newLike = PostLikes.builder()
                .post(targetPost)
                .communityProfile(targetCommuProfile)
                .build();

        targetPost.addLike();

        return postLikesRepository.save(newLike);
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

        postLikesRepository.delete(target);
        targetPost.removeLike();
        return target;
    }
}
