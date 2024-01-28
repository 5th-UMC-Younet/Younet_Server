package com.example.younet.post.service;

import com.example.younet.aws.AmazonS3Manager;
import com.example.younet.domain.*;
import com.example.younet.post.dto.PostRequestDTO;
import com.example.younet.post.dto.PostResponseDTO;
import com.example.younet.post.dto.SectionDTO;
import com.example.younet.post.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
    private final CommunityProfileRepository communityProfileRepository;
    private final CountryRepository countryRepository;
    private final CategoryRepository categoryRepository;

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

    public List<Image> addImages(Long postId, MultipartFile[] files){
        List<Image> images = new ArrayList<>();
        Post post = postRepository.findById(postId).get();
        for (MultipartFile file : files) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());
            String imageUrl = s3Manager.uploadFile(s3Manager.generateKeyName(savedUuid), file);
            //images.add(imageRepository.save(Image.builder().imageUrl(imageUrl).post(post).build()));
        }
        return images;
    }

    public Post addPost(PostRequestDTO.AddPostDTO request, List<MultipartFile> files) throws IOException{

        CommunityProfile communityProfile=communityProfileRepository.findById(request.getCommunityProfileId()).get();
        Country country=countryRepository.findById(request.getCountryId()).get();
        Category category=categoryRepository.findById(request.getCategoryId()).get();

        Post post=Post.builder()
                .title(request.getTitle())
                .communityProfile(communityProfile)
                .country(country)
                .category(category)
                .sections(new ArrayList<>())
                .build();

        for (SectionDTO sectionDTO: request.getSections()){
            Section section=Section.builder()
                    .body(sectionDTO.getBody())
                    .post(post)
                    .images(new ArrayList<>())
                    .build();
            for (String imageKey: sectionDTO.getImageKeys()){
                MultipartFile file=files.stream()
                        .filter(f -> f.getOriginalFilename().equals(imageKey))
                        .findFirst()
                        .orElseThrow(() -> new FileNotFoundException("File not found: " + imageKey));

                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = uuidRepository.save(Uuid.builder()
                        .uuid(uuid).build());
                String imageUrl = s3Manager.uploadFile(s3Manager.generateKeyName(savedUuid), file);
                Image image= Image.builder()
                        .imageUrl(imageUrl)
                        .section(section)
                        .build();

                section.getImages().add(image);
            }

            post.getSections().add(section);
        }

        return postRepository.save(post);
    }

}
