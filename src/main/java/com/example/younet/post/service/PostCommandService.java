package com.example.younet.post.service;

import com.example.younet.aws.AmazonS3Manager;
import com.example.younet.domain.Image;
import com.example.younet.domain.Post;
import com.example.younet.domain.Uuid;
import com.example.younet.post.dto.PostRequestDTO;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.post.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandService {
    private final PostRepository postRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;
    private final ImageRepository imageRepository;

    public Slice<PostResponseDTO.postListResultDTO> getPostListByDates
            (Long lastPostId, Long categoryId, Long countryId){
        Pageable pageable=PageRequest.of(0,10); // size: 10
        return postRepository.getPostListByDates(lastPostId, categoryId, countryId, pageable);
    }
    public Slice<PostResponseDTO.postListResultDTO> getPostListByLikes
            (Long lastPostId, Long categoryId, Long countryId){
        Pageable pageable=PageRequest.of(0,10); // size: 10
        return postRepository.getPostListByLikes(lastPostId, categoryId, countryId, pageable);
    }

    public Image addImage(Long postId, MultipartFile file){
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
        String imageUrl=s3Manager.uploadFile(s3Manager.generateKeyName(savedUuid),file);

        Post post=postRepository.findById(postId).get();
        return imageRepository.save(Image.builder().imageUrl(imageUrl).post(post).build());
    }
}
