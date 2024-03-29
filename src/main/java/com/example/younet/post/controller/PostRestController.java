package com.example.younet.post.controller;

import com.example.younet.ApiPayload.ApiResponse;
import com.example.younet.domain.Post;
import com.example.younet.post.converter.PostConverter;
import com.example.younet.post.dto.CategoryResponseDTO;
import com.example.younet.post.dto.PostRequestDTO;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.post.repository.PostRepository;
import com.example.younet.post.service.PostCommandService;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ApiResponse<PostResponseDTO.AddPostResultDTO> addPost
            (@RequestPart("post") PostRequestDTO.AddPostDTO request,
             @Nullable @RequestPart("files") List<MultipartFile> files) throws IOException {
        Post post=postCommandService.addPost(request, files);
        return ApiResponse.onSuccess(HttpStatus.CREATED,PostConverter.toAddPostResultDTO(post));
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponseDTO.CategoryListResultDTO>> showCategoryList(){
        return ApiResponse.onSuccess(HttpStatus.OK,postCommandService.categoryList());
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponseDTO.SelectedPostResultDTO> getAllOfPostContentsById(@PathVariable Long postId){
        return ApiResponse.onSuccess(HttpStatus.OK, postCommandService.getAllOfPostContentById(postId));
    }

    @GetMapping("{postId}/comment_reply_cnt")
    public ApiResponse<PostResponseDTO.SelectedPostCommentAndReplyCntDTO> getCommentAndReplyCnt(@PathVariable Long postId){
        return ApiResponse.onSuccess(HttpStatus.OK, postCommandService.getCommentAndReplyList(postId));
    }
}
