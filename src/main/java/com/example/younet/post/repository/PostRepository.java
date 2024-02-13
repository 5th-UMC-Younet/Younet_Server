package com.example.younet.post.repository;

import com.example.younet.domain.Post;
import com.example.younet.domain.Scrap;
import com.example.younet.post.repository.CustomRepository.PostRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryCustom {
    List<Post> findAllByCommunityProfile_Id(Long communityProfileId);

    List<Post> findByIdInOrderByCreatedAtDesc(List<Long> postIds);
}