package com.example.younet.post.controller;

import com.example.younet.post.dto.PostRequestDTO;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.post.service.PostCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostRestController {
    private final PostCommandService postCommandService;
    @PostMapping("/")
    // 게시물 등록 임시 코드
    public String post(@RequestBody PostRequestDTO.JoinDTO request){
        return postCommandService.addPost(request);
    }
    // post 조회 #1 카테고리별. (국가, pagination x)
    @GetMapping("/list")
    public List<PostResponseDTO.postListResultDTO> getPostList(@RequestParam("category")Long categoryId){
        return postCommandService.getPostListByCategory(categoryId);
    }

    // post 조회 #2 pagination, order by 최신순
    @GetMapping("/list2")
    public List<PostResponseDTO.postListResultDTO> getPostListWithPageAndOrderByDate(@RequestParam("page")int page,@RequestParam("size") int size){
        return postCommandService.getPostListWithPageAndOrderByDate(page,size);
    }
}
