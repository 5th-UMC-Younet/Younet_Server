package com.example.younet.post.repository;

import com.example.younet.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepositoryCustom {
    // QueryDsl
    List<Post> getPostList(Long categoryId);
    List<Post> getPostListWithPageAndOrder(Long offset, int pageSize);
}
