package com.example.younet.userprofile.mypage.service;

import com.example.younet.domain.Post;
import com.example.younet.domain.User;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.repository.UserRepository;
import com.example.younet.userprofile.mypage.dto.UserProfileDto;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;
    //private ScrapRepository scrapRepository;

    public UserProfileDto.UserResultDTO findUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));

        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<UserProfileDto.userProfilePostDTO> postDTOs = posts.stream()
                .map(post -> new UserProfileDto.userProfilePostDTO(
                        post.getRepresentativeImage(),
                        post.getTitle(),
                        post.getIntroduction(),
                        post.getCreatedAt(),
                        post.getLikesCount()
                        //post.getCommentsCount()
                ))
                .collect(Collectors.toList());

        return UserProfileDto.UserResultDTO.builder()
                .profilePicture(user.getProfilePicture())
                .name(user.getName())
                .hostCntr(user.getHostContr())
                .profileText(user.getProfileText())
                .posts(postDTOs)
                .build();
    }
}
