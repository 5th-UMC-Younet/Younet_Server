package com.example.younet.post.converter;

import com.example.younet.domain.Image;
import com.example.younet.domain.Section;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageConverter {
    public static Image toImage(String imageUrl,Section newSection){
        return Image.builder()
                .section(newSection)
                .imageUrl(imageUrl)
                .build();
    }
}
