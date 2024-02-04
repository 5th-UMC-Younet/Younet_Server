package com.example.younet.userprofile.mypage.service;

import com.example.younet.comment.repository.CommentRepository;
import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.Post;
import com.example.younet.domain.Scrap;
import com.example.younet.domain.User;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.scrap.repository.ScrapRepository;
import com.example.younet.userprofile.mypage.dto.MyPageDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final ScrapRepository scrapRepository;

    public MyPageDto.MyProfileDTO myPageInfo(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        Long userId = user.getId();

        CommunityProfile communityProfile = communityProfileRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        Long communityProfileId = communityProfile.getUser().getId();

        List<Post> myPosts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Scrap> scrapLists = scrapRepository.findAllByCommunityProfile_Id(communityProfileId);
        List<Long> postIds = scrapLists.stream()
                .map(scrap -> scrap.getPost().getId())
                .collect(Collectors.toList());
        List<Post> scrapPosts = postRepository.findByIdInOrderByCreatedAtDesc(postIds);

        List<MyPageDto.MyProfilePostDTO> postDTOs = myPosts.stream()
                .map(post -> new MyPageDto.MyProfilePostDTO(
                        post.getRepresentativeImage(),
                        post.getTitle(),
                        post.getIntroduction(),
                        post.getCreatedAt(),
                        post.getLikesCount(),
                        commentRepository.countCommentsByPostId(post.getId())
                ))
                .collect(Collectors.toList());

        List<MyPageDto.MyProfilePostDTO> scrapDTOs = scrapPosts.stream()
                .map(post -> new MyPageDto.MyProfilePostDTO(
                        post.getRepresentativeImage(),
                        post.getTitle(),
                        post.getIntroduction(),
                        post.getCreatedAt(),
                        post.getLikesCount(),
                        commentRepository.countCommentsByPostId(post.getId())
                ))
                .collect(Collectors.toList());

        return MyPageDto.MyProfileDTO.builder()
                .userId(communityProfile.getUser().getId())
                .profilePicture(communityProfile.getUser().getProfilePicture())
                .name(communityProfile.getUser().getName())
                .nickname(communityProfile.getUser().getNickname())
                .likeCntr(communityProfile.getCountry().getName())
                .profileText(communityProfile.getUser().getProfileText())
                .posts(postDTOs)
                .scraps(scrapDTOs)
                .build();
    }
}
