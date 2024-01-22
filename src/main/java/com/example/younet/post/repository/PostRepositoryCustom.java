package com.example.younet.post.repository;

import com.example.younet.domain.Post;
import com.example.younet.post.dto.PostResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepositoryCustom {
    // QueryDsl
    List<Post> getPostList(Long categoryId);
    List<Post> getPostListWithPageAndOrder(Long offset, int pageSize);
    Slice<PostResponseDTO.postListResultDTO> getBySlice(Long lastPostId, Long categoryId, Long countryId, Pageable pageable);
}
