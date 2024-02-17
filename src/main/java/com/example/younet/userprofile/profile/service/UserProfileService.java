package com.example.younet.userprofile.profile.service;

import com.example.younet.comment.repository.CommentRepository;
import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.Country;
import com.example.younet.domain.Image;
import com.example.younet.domain.Post;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.ImageRepository;
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
    private final ImageRepository imageRepository;

    public UserProfileDto.UserResultDTO getUserProfileInfo(Long userId) {
        CommunityProfile communityProfile = communityProfileRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        Long communityProfileId = communityProfile.getUser().getId();

        List<Post> posts = postRepository.findAllByCommunityProfile_Id(communityProfileId);

        List<UserProfileDto.userProfilePostDTO> postDTOs = posts.stream()
                .map(post -> {
                    Image representativeImage = imageRepository.findByName(post.getRepresentativeImage());
                    String imageUrl = representativeImage.getImageUrl();
                    return new UserProfileDto.userProfilePostDTO(
                            post.getId(),
                            post.getCategory().getId(),
                            imageUrl,
                            post.getTitle(),
                            post.getIntroduction(),
                            post.getCreatedAt(),
                            post.getLikesCount(),
                            commentRepository.countCommentsByPostId(post.getId())
                    );
                })
                .collect(Collectors.toList());

        String likeCntr = null;
        Country country = communityProfile.getCountry();
        if (country != null) {
            likeCntr = country.getName();
        }

        return UserProfileDto.UserResultDTO.builder()
                .communityProfileId(communityProfile.getId())
                .userId(communityProfile.getUser().getId())
                .profilePicture(communityProfile.getProfilePicture())
                .name(communityProfile.getUser().getName())
                .likeCntr(likeCntr)
                .profileText(communityProfile.getUser().getProfileText())
                .posts(postDTOs)
                .build();
    }
}
