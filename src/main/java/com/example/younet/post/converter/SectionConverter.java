package com.example.younet.post.converter;

import com.example.younet.domain.Post;
import com.example.younet.domain.Section;
import com.example.younet.post.dto.SectionDTO;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor
public class SectionConverter {
    public static Section toSection(SectionDTO sectionDTO, Post newPost){
        return Section.builder()
                .body(sectionDTO.getBody())
                .post(newPost)
                .images(new ArrayList<>())
                .build();
    }
}
