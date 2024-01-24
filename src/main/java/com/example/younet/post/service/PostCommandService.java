package com.example.younet.post.service;

import com.example.younet.domain.Post;
import com.example.younet.post.dto.PostRequestDTO;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.post.repository.CategoryRepository;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.post.repository.CountryRepository;
import com.example.younet.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommandService {
    private final PostRepository postRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final CountryRepository countryRepository;
    private final CategoryRepository categoryRepository;

    public String addPost(PostRequestDTO.JoinDTO request){
        Post newPost=Post.builder().title(request.getTitle()).body(request.getBody()).build();
        newPost.setCommunityProfile(communityProfileRepository.findById(request.getCommunityProfileId()).get());
        newPost.setCategory(categoryRepository.findById(request.getCategoryId()).get());
        newPost.setCountry(countryRepository.findById(request.getCountryId()).get());
        postRepository.save(newPost);
        return "ok";
    }

    public Slice<PostResponseDTO.postListResultDTO> getPostListByDates
            (Long lastPostId, Long categoryId, Long countryId){
        Pageable pageable=PageRequest.of(0,3); // size: 10
        return postRepository.getPostListByDates(lastPostId, categoryId, countryId, pageable);
    }
    public Slice<PostResponseDTO.postListResultDTO> getPostListByLikes
            (Long lastPostId, Long categoryId, Long countryId){
        Pageable pageable=PageRequest.of(0,4); // size: 10
        return postRepository.getPostListByLikes(lastPostId, categoryId, countryId, pageable);
    }

}
