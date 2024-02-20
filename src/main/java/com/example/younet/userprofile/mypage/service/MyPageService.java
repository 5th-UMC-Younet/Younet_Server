package com.example.younet.userprofile.mypage.service;

import com.example.younet.aws.AmazonS3Manager;
import com.example.younet.comment.repository.CommentRepository;
import com.example.younet.domain.*;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.CountryRepository;
import com.example.younet.post.repository.ImageRepository;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.scrap.repository.ScrapRepository;
import com.example.younet.userprofile.mypage.dto.MyPageDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.PrinterGraphics;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final ScrapRepository scrapRepository;
    private final CountryRepository countryRepository;
    private final ImageRepository imageRepository;
    private final AmazonS3Manager s3Manager;

    public MyPageDto.MyProfileDTO getMyPageInfo(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        Long userId = user.getId();

        CommunityProfile communityProfile = communityProfileRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        Long communityProfileId = communityProfile.getUser().getId();

        List<Post> myPosts = postRepository.findAllByCommunityProfile_Id(communityProfileId);
        List<Scrap> scrapLists = scrapRepository.findAllByCommunityProfile_Id(communityProfileId);
        List<Long> postIds = scrapLists.stream()
                .map(scrap -> scrap.getPost().getId())
                .collect(Collectors.toList());
        List<Post> scrapPosts = postRepository.findByIdInOrderByCreatedAtDesc(postIds);

        List<MyPageDto.MyProfilePostDTO> postDTOs = myPosts.stream()
                .map(post -> {
                    Image representativeImage = imageRepository.findByName(post.getRepresentativeImage());
                    String imageUrl = null;

                    if (representativeImage != null && representativeImage.getImageUrl() != null) {
                        imageUrl = representativeImage.getImageUrl();
                    }

                    return new MyPageDto.MyProfilePostDTO(
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

        List<MyPageDto.MyProfilePostDTO> scrapDTOs = scrapPosts.stream()
                .map(post -> {
                    Image representativeImage = imageRepository.findByName(post.getRepresentativeImage());
                    String imageUrl = null;

                    if (representativeImage != null && representativeImage.getImageUrl() != null) {
                        imageUrl = representativeImage.getImageUrl();
                    }

                    return new MyPageDto.MyProfilePostDTO(
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

        return MyPageDto.MyProfileDTO.builder()
                .userId(communityProfile.getUser().getId())
                .profilePicture(communityProfile.getProfilePicture())
                .name(communityProfile.getName())
                .likeCntr(likeCntr)
                .profileText(communityProfile.getProfileText())
                .posts(postDTOs)
                .scraps(scrapDTOs)
                .build();
    }

    public MyPageDto.MyProfileInfoDTO getMyPageInfoEdit(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        Long userId = user.getId();

        CommunityProfile communityProfile = communityProfileRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        Long communityProfileId = communityProfile.getUser().getId();

        String likeCntr = null;
        Country country = communityProfile.getCountry();
        if (country != null) {
            likeCntr = country.getName();
        }

        return MyPageDto.MyProfileInfoDTO.builder()
                .profilePicture(communityProfile.getProfilePicture())
                .name(user.getName())
                .nickname(user.getNickname())
                .likeCntr(likeCntr)
                .profileText(communityProfile.getProfileText())
                .build();
    }

    public void patchEditMyPageInfo(PrincipalDetails principalDetails, MyPageDto.MyProfileEditDTO myProfileEditDTO, MultipartFile file) {
        User user = principalDetails.getUser();
        Long userId = user.getId();

        String imageUrl = s3Manager.uploadFile(generateUniqueFileName(userId.toString()), file);

        CommunityProfile communityProfile = communityProfileRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        Long communityProfileId = communityProfile.getUser().getId();

        Country country = countryRepository.findByName(myProfileEditDTO.getLikeCntr());

        // 유저 수정
        communityProfile.getUser().setName(myProfileEditDTO.getName());
        communityProfile.getUser().setNickname(myProfileEditDTO.getNickname());
        // 커뮤니티 프로필 수정
        communityProfile.setProfileText(myProfileEditDTO.getProfileText());
        communityProfile.setProfilePicture(imageUrl);
        communityProfile.setName(myProfileEditDTO.getNickname());
        communityProfile.setCountry(country);

        communityProfileRepository.save(communityProfile);
        return ;
    }

    private String generateUniqueFileName(String userId) {
        UUID uuid = UUID.randomUUID();
        String uniqueFileName = userId + "_" + uuid.toString();
        return uniqueFileName;
    }
}
