package com.example.younet.post.controller;

import com.example.younet.ApiPayload.ApiResponse;
import com.example.younet.post.dto.CountryResponseDTO;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.post.service.PostCommandService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostCommunityController {
    private final PostCommandService postCommandService;
    private final PostRepository postRepository;

    @GetMapping("/countries")
    public ApiResponse<List<CountryResponseDTO.CountryListResultDTO>> showCountryList(){
        return ApiResponse.onSuccess(HttpStatus.OK,postCommandService.countryList());
    }
    @GetMapping("/{countryId}/{categoryId}/byDates")
    public Slice<PostResponseDTO.postListResultDTO> getPostListWithSliceAndOrderByDates
            (@Nullable @RequestParam("lastpost") Long postId, @PathVariable long countryId, @PathVariable long categoryId)
    {
        return postCommandService.getPostListByDates(postId,categoryId,countryId);
    }
    @GetMapping("/{countryId}/{categoryId}/byLikes")
    public Slice<PostResponseDTO.postListResultDTO> getPostListWithSliceAndOrderByLikes
            (@Nullable @RequestParam("lastpost") Long postId, @PathVariable long countryId, @PathVariable long categoryId)
    {
        return postCommandService.getPostListByLikes(postId,categoryId,countryId);
    }

    @GetMapping("/search/{keyword}")
    public Slice<PostResponseDTO.postListResultDTO> getSearchResult(@PathVariable String keyword){
        Pageable pageable= PageRequest.of(0,10);
        return postRepository.getPostList(keyword,pageable);
    }

}
