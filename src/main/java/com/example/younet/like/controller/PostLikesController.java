package com.example.younet.like.controller;


import com.example.younet.ApiPayload.ApiResponse;
import com.example.younet.like.dto.LikeRequestDTO;
import com.example.younet.like.dto.LikeResponseDTO;
import com.example.younet.like.repository.PostLikesRepository;
import com.example.younet.like.service.PostLikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/like")
@RequiredArgsConstructor
@RestController
public class PostLikesController {
    private final PostLikesService postLikesService;

    @PostMapping("post")
    public ApiResponse<LikeResponseDTO.Add> addLike(@RequestBody LikeRequestDTO.Add requestDto) {

        LikeResponseDTO.Add response = new LikeResponseDTO.Add(postLikesService.addLike(requestDto));
        return ApiResponse.onSuccess(HttpStatus.OK, response);
    }

    @GetMapping("post")
    public ApiResponse<LikeResponseDTO.CountPostLikes> removeLike(@RequestBody LikeRequestDTO.CountPostLikes requestDto) {

        LikeResponseDTO.CountPostLikes response = LikeResponseDTO.CountPostLikes.builder()
                .postId(requestDto.getPostId())
                .likeCount(postLikesService.countLike(requestDto))
                .build();

        return ApiResponse.onSuccess(HttpStatus.OK, response);
    }


    @DeleteMapping("post")
    public ApiResponse<LikeResponseDTO.Remove> removeLike(@RequestBody LikeRequestDTO.Remove requestDto) {

        LikeResponseDTO.Remove response = new LikeResponseDTO.Remove(postLikesService.removeLike(requestDto));
        return ApiResponse.onSuccess(HttpStatus.OK, response);
    }
}
