package com.example.younet.post.repository;

import com.example.younet.post.dto.PostResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryCustom {
    // QueryDsl
    Slice<PostResponseDTO.postListResultDTO> getBySlice(Long lastPostId, Long categoryId, Long countryId, Pageable pageable);
}
