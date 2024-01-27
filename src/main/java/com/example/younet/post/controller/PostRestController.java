package com.example.younet.post.controller;

import com.example.younet.domain.Image;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostRestController {
    private final PostCommandService postCommandService;
    private final PostRepository postRepository;
    @GetMapping("/like")
    // 좋아요 누르기 임시 코드
    @Transactional
    public String doLike(@RequestParam("post")Long postId){
        Post post=postRepository.findById(postId).get();
        post.addLike();
        postRepository.save(post);
        return "ok";
    }
    @PostMapping("/")
    public String addPost(){
        return null;
    }
    @PostMapping(value = "/image",consumes = "multipart/form-data")
    public String addPostImage(@RequestParam("post")Long postId, MultipartFile file){
        Image image=postCommandService.addImage(postId,file);
        if (image!=null)
            return "ok";
        return null;
    }
    // 다중 이미지
    @PostMapping(value = "/images",consumes = "multipart/form-data")
    public String addPostImages(@RequestParam("post")Long postId, @RequestParam("files") MultipartFile[] files){
        List<Image> images=postCommandService.addImages(postId,files);
        if (images!=null && !images.isEmpty())
            return "ok";
        return null;
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
