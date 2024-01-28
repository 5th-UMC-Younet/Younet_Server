package com.example.younet.post.converter;

import com.example.younet.domain.Post;
import com.example.younet.post.dto.PostRequestDTO;
import com.example.younet.post.dto.PostResponseDTO;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RequiredArgsConstructor
public class PostConverter {
    public static PostResponseDTO.AddPostResultDTO toAddPostResultDTO(Post post){
        return PostResponseDTO.AddPostResultDTO.builder()
                .postId(post.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Post toPost(PostRequestDTO.AddPostDTO request){
        return Post.builder()
                .title(request.getTitle())
                .sections(new ArrayList<>())
                .build();
    }

}
