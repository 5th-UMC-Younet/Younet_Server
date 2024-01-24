package com.example.younet.post.controller;

import com.example.younet.domain.Post;
import com.example.younet.post.dto.PostRequestDTO;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.post.service.PostCommandService;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostRestController {
    private final PostCommandService postCommandService;
    private final PostRepository postRepository;
    @PostMapping("/")
    // 게시물 등록 임시 코드
    public String post(@RequestBody PostRequestDTO.JoinDTO request){
        return postCommandService.addPost(request);
    }

    @GetMapping("/like")
    // 좋아요 누르기 임시 코드
    @Transactional
    public String doLike(@RequestParam("post")Long postId){
        Post post=postRepository.findById(postId).get();
        post.addLike();
        postRepository.save(post);
        return "ok";
    }

    @GetMapping("/byDates")
    public Slice<PostResponseDTO.postListResultDTO> getPostListWithSliceAndOrderByDates
            (@Nullable @RequestParam("lastpost") Long postId, @RequestParam("category") long categoryId, @RequestParam("country") long countryId)
    {
        return postCommandService.getPostListByDates(postId,categoryId,countryId);
    }
    @GetMapping("/byLikes")
    public Slice<PostResponseDTO.postListResultDTO> getPostListWithSliceAndOrderByLikes
            (@Nullable @RequestParam("lastpost") Long postId, @RequestParam("category") long categoryId, @RequestParam("country") long countryId)
    {
        return postCommandService.getPostListByLikes(postId,categoryId,countryId);
    }
}
