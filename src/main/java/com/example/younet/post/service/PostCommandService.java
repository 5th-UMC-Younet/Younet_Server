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
    public List<PostResponseDTO.postListResultDTO> getPostListByCategory(Long categoryId){
        List<Post> posts=postRepository.getPostList(categoryId);
        return posts.stream()
                .map(post -> PostResponseDTO.postListResultDTO.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .body(post.getBody())
                        .categoryName(post.getCategory().getName())
                        .build()
                ).collect(Collectors.toList());
    }

    public List<PostResponseDTO.postListResultDTO> getPostListWithPageAndOrderByDate(int page, int size){
        PageRequest pageRequest=PageRequest.of(page,size);

        return postRepository.getPostListWithPageAndOrder(pageRequest.getOffset(), pageRequest.getPageSize())
                .stream()
                .map(post -> PostResponseDTO.postListResultDTO.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .body(post.getBody())
                        .categoryName(post.getCategory().getName())
                        .build()
                ).collect(Collectors.toList());
    }

    public Slice<PostResponseDTO.postListResultDTO> getPostListWithSliceAndOrderByDate
            (Long lastPostId, Long categoryId, Long countryId, Pageable pageable){
        return postRepository.getBySlice(lastPostId, categoryId, countryId, pageable);
    }

}
