package com.example.younet.userprofile.profile.service;

import com.example.younet.comment.repository.CommentRepository;
import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.Post;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.userprofile.profile.dto.UserProfileDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityProfileRepository communityProfileRepository;

    public UserProfileDto.UserResultDTO findUserInfo(Long userId) {
        CommunityProfile communityProfile = communityProfileRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));

        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<UserProfileDto.userProfilePostDTO> postDTOs = posts.stream()
                .map(post -> new UserProfileDto.userProfilePostDTO(
                        post.getRepresentativeImage(),
                        post.getTitle(),
                        post.getIntroduction(),
                        post.getCreatedAt(),
                        post.getLikesCount(),
                        commentRepository.countCommentsByPostId(post.getId())
                ))
                .collect(Collectors.toList());

        return UserProfileDto.UserResultDTO.builder()
                .userId(communityProfile.getUser().getId())
                .profilePicture(communityProfile.getProfilePicture())
                .name(communityProfile.getUser().getName())
                .likeCntr(communityProfile.getCountry().getName())
                .profileText(communityProfile.getUser().getProfileText())
                .posts(postDTOs)
                .build();
    }
}
