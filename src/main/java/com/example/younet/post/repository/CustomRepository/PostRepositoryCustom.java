package com.example.younet.post.repository.CustomRepository;

import com.example.younet.post.dto.PostResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryCustom {
    // QueryDsl
    Slice<PostResponseDTO.postListResultDTO> getPostListByDates(Long lastPostId, Long categoryId, Long countryId, Pageable pageable);
    Slice<PostResponseDTO.postListResultDTO> getPostListByLikes(Long lastPostId, Long categoryId, Long countryId, Pageable pageable);

    Slice<PostResponseDTO.postListResultDTO> searchPostListByLikes(Long lastPostId,Long countryId,Long categoryId, String keyword, Pageable pageable);
    Slice<PostResponseDTO.postListResultDTO> searchPostListByDates(Long lastPostId,Long countryId,Long categoryId, String keyword, Pageable pageable);
}
