package com.example.younet.post.service;

import com.example.younet.aws.AmazonS3Manager;
import com.example.younet.domain.*;
import com.example.younet.post.converter.ImageConverter;
import com.example.younet.post.converter.PostConverter;
import com.example.younet.post.converter.SectionConverter;
import com.example.younet.post.dto.*;
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

    public List<CategoryResponseDTO.CategoryListResultDTO> categoryList(){
        return categoryRepository.findAll().stream()
                .map(category -> CategoryResponseDTO.CategoryListResultDTO.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CountryResponseDTO.CountryListResultDTO> countryList(){
        return countryRepository.findAll().stream()
                .map(country -> CountryResponseDTO.CountryListResultDTO.builder()
                        .countryId(country.getId())
                        .countryName(country.getName())
                        .build()
                ).collect(Collectors.toList());
    }

    public Post addPost(PostRequestDTO.AddPostDTO request, List<MultipartFile> files) throws IOException{

        CommunityProfile communityProfile=communityProfileRepository.findById(request.getCommunityProfileId()).get();
        Country country=countryRepository.findById(request.getCountryId()).get();
        Category category=categoryRepository.findById(request.getCategoryId()).get();

        Post newPost= PostConverter.toPost(request);
        newPost.setCommunityProfile(communityProfile);
        newPost.setCountry(country);
        newPost.setCategory(category);

        int i=0;
        for (SectionDTO sectionDTO: request.getSections()){
            Section newSection= SectionConverter.toSection(sectionDTO,newPost);
            for (String imageKey: sectionDTO.getImageKeys()){
                MultipartFile file=files.stream()
                        .filter(f -> f.getOriginalFilename().equals(imageKey))
                        .findFirst()
                        .orElseThrow(() -> new FileNotFoundException("File not found: " + imageKey));

                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = uuidRepository.save(Uuid.builder()
                        .uuid(uuid).build());
                String imageUrl = s3Manager.uploadFile(s3Manager.generateKeyName(savedUuid), file);
                Image image= ImageConverter.toImage(savedUuid.getUuid(),imageUrl,newSection);
                if (i==0) {
                    newPost.setRepresentativeImage(savedUuid.getUuid());
                    i++;
                }
                newSection.getImages().add(image);
            }

            newPost.getSections().add(newSection);
        }

        return postRepository.save(newPost);
    }

    public PostResponseDTO.SelectedPostResultDTO getAllOfPostContentById(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.getSections().forEach(Section::getImages);
        return PostConverter.of(post);
    }
}
