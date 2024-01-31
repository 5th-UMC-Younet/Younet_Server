package com.example.younet.scrap.controller;


import com.example.younet.ApiPayload.ApiResponse;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.scrap.dto.ScrapRequestDTO;
import com.example.younet.scrap.dto.ScrapResponseDTO;
import com.example.younet.scrap.service.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/scrap")
@RequiredArgsConstructor
@RestController
public class ScrapController {
    private final ScrapService scrapService;

    @PostMapping("post")
    public ApiResponse<ScrapResponseDTO.Add> addScrap(@RequestBody ScrapRequestDTO.Add requestDto) {

        ScrapResponseDTO.Add response = new ScrapResponseDTO.Add(scrapService.addScrap(requestDto));
        return ApiResponse.onSuccess(HttpStatus.OK, response);
    }

    @GetMapping("post")
    public ApiResponse<Slice<PostResponseDTO.postListResultDTO>> getScrappedPostsWithSlice(@RequestBody ScrapRequestDTO.GetScrappedPosts requestDto) {

        Slice<PostResponseDTO.postListResultDTO> response = scrapService.getScrappedPostsWithSlice(requestDto)
            .map((element) -> PostResponseDTO.postListResultDTO.builder()
                .postId(element.getId())
                .title(element.getTitle())
                .likesCount(element.getLikesCount())
                .categoryName(element.getCategory().getName())
                .build());

        return ApiResponse.onSuccess(HttpStatus.OK, response);
    }


    @DeleteMapping("post")
    public ApiResponse<ScrapResponseDTO.Remove> removeScrap(@RequestBody ScrapRequestDTO.Remove requestDto) {

        ScrapResponseDTO.Remove response = new ScrapResponseDTO.Remove(scrapService.removeScrap(requestDto));
        return ApiResponse.onSuccess(HttpStatus.OK, response);
    }
}
