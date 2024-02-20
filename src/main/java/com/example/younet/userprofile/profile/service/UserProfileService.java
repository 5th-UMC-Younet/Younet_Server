package com.example.younet.userprofile.profile.service;

import com.example.younet.aws.AmazonS3Manager;
import com.example.younet.comment.repository.CommentRepository;
import com.example.younet.domain.*;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.ImageRepository;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.repository.UserRepository;
import com.example.younet.userprofile.mypage.dto.MyPageDto;
import com.example.younet.userprofile.profile.dto.UserProfileDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final AmazonS3Manager s3Manager;

    public UserProfileDto.UserResultDTO getUserProfileInfo(Long userId) {
        CommunityProfile communityProfile = communityProfileRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        Long communityProfileId = communityProfile.getUser().getId();

        List<Post> posts = postRepository.findAllByCommunityProfile_Id(communityProfileId);

        List<UserProfileDto.userProfilePostDTO> postDTOs = posts.stream()
                .map(post -> {
                    String imageUrl = null;
                    Image representativeImage = imageRepository.findByName(post.getRepresentativeImage());
                    if (representativeImage != null && representativeImage.getImageUrl() != null) {
                        imageUrl = representativeImage.getImageUrl();
                    }

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
                .name(communityProfile.getName())
                .likeCntr(likeCntr)
                .profileText(communityProfile.getProfileText())
                .posts(postDTOs)
                .build();
    }

    public void patchEditUserProfileInfo(PrincipalDetails principalDetails, UserProfileDto.UserProfileEditDTO userProfileEditDTO, MultipartFile file) {
        User user = principalDetails.getUser();
        Long userId = user.getId();

        String imageUrl = s3Manager.uploadFile(generateUniqueFileName(userId.toString()), file);

        user.setProfilePicture(imageUrl);
        user.setProfileText(userProfileEditDTO.getProfileText());
        userRepository.save(user);
        return;
    }

    private String generateUniqueFileName(String userId) {
        UUID uuid = UUID.randomUUID();
        String uniqueFileName = userId + "_" + uuid.toString();
        return uniqueFileName;
    }
}
