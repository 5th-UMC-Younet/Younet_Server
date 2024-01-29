package com.example.younet.post.converter;

import com.example.younet.domain.Post;
import com.example.younet.domain.Section;
import com.example.younet.post.dto.ImageResponseDTO;
import com.example.younet.post.dto.SectionDTO;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SectionConverter {
    public static Section toSection(SectionDTO sectionDTO, Post newPost){
        return Section.builder()
                .body(sectionDTO.getBody())
                .post(newPost)
                .images(new ArrayList<>())
                .build();
    }

    public static SectionDTO.SectionResultDTO sectionResultDTO(Section section, List<ImageResponseDTO.ImageResultDTO> imageResultDTOs){
        return SectionDTO.SectionResultDTO.builder()
                .id(section.getId())
                .body(section.getBody())
                .images(imageResultDTOs)
                .build();
    }
}
