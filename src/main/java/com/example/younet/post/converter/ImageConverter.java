package com.example.younet.post.converter;

import com.example.younet.domain.Image;
import com.example.younet.domain.Section;
import com.example.younet.post.dto.ImageResponseDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageConverter {
    public static Image toImage(String uuid, String imageUrl,Section newSection){
        return Image.builder()
                .section(newSection)
                .imageUrl(imageUrl)
                .name(uuid)
                .build();
    }

    public static ImageResponseDTO.ImageResultDTO imageResultDTO(Image image){
        return ImageResponseDTO.ImageResultDTO.builder()
                .ImageId(image.getId())
                .imageUrl(image.getImageUrl())
                .build();
    }
}
