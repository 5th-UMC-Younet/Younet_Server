package com.example.younet.scrap.service;


import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.Post;
import com.example.younet.domain.PostLikes;
import com.example.younet.domain.Scrap;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.scrap.dto.ScrapRequestDTO;
import com.example.younet.scrap.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final CommunityProfileRepository communityProfileRepository;

    @Transactional
    public Scrap addScrap(ScrapRequestDTO.Add requestDto) {
        Post targetPost = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post ID를 찾을 수 없습니다."));
        CommunityProfile targetCommuProfile = communityProfileRepository.findById(requestDto.getCommunityProfileId())
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티 프로필 ID를 찾을 수 없습니다."));

        Scrap newScrap = Scrap.builder()
                .post(targetPost)
                .communityProfile(targetCommuProfile)
                .build();

        return scrapRepository.save(newScrap);
    }

    @Transactional
    public Slice<Post> getScrappedPostsWithSlice(ScrapRequestDTO.GetScrappedPosts requestDto) {

        PageRequest pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        return scrapRepository.findSliceByCommunityProfile_id(requestDto.getCommunityProfileId(), pageable)
                .map(Scrap::getPost);

    }


    @Transactional
    public Scrap removeScrap(ScrapRequestDTO.Remove requestDto) {
        Scrap target = scrapRepository.findByPost_IdAndCommunityProfile_Id(requestDto.getPostId(), requestDto.getCommunityProfileId())
                .orElseThrow(() -> new IllegalArgumentException("Post ID, 혹은 커뮤니티 프로필 ID를 찾을 수 없습니다."));

        Post targetPost = target.getPost();

        scrapRepository.delete(target);
        return target;
    }
}
