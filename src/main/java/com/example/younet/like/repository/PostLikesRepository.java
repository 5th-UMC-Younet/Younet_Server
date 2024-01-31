package com.example.younet.like.repository;

import com.example.younet.domain.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikesRepository extends JpaRepository<PostLikes, Long> {
    Optional<PostLikes> findByPost_IdAndCommunityProfile_Id(Long postId, Long communityProfileId);
    List<PostLikes> findAllByPost_Id(Long postId);
    List<PostLikes> findAllByCommunityProfile_Id(Long communityProfileId);



}
