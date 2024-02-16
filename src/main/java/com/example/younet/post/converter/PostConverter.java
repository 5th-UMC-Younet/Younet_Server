package com.example.younet.post.converter;

import com.example.younet.domain.Post;
import com.example.younet.post.dto.ImageResponseDTO;
import com.example.younet.post.dto.PostRequestDTO;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.post.dto.SectionDTO;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static PostResponseDTO.SelectedPostResultDTO of(Post post, String authorName,long commentsCount){
        List<SectionDTO.SectionResultDTO> sectionResultDTOs=post.getSections().stream()
                .map(section -> {
                    List<ImageResponseDTO.ImageResultDTO> imageResultDTOs= section.getImages().stream()
                            .map(ImageConverter::imageResultDTO).collect(Collectors.toList());

                    return SectionConverter.sectionResultDTO(section,imageResultDTOs);
                }).collect(Collectors.toList());
        return PostResponseDTO.SelectedPostResultDTO.builder()
                .postId(post.getId())
                .authorCommuProfId(post.getCommunityProfile().getId())
                .authorName(authorName)
                .postTitle(post.getTitle())
                .likesCount(post.getLikesCount())
                .sections(sectionResultDTOs)
                .commentsCount(commentsCount)
                .createdAt(post.getCreatedAt())
                .build();
    }
}