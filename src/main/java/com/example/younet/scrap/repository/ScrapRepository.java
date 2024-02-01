package com.example.younet.scrap.repository;

import com.example.younet.domain.Post;
import com.example.younet.domain.PostLikes;
import com.example.younet.domain.Scrap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Slice<Scrap> findSliceByCommunityProfile_id(Long communityProfileId, Pageable pageable);
    Optional<Scrap> findByPost_IdAndCommunityProfile_Id(Long postId, Long communityProfileId);
    List<Scrap> findAllByPost_Id(Long postId);
    List<Scrap> findAllByCommunityProfile_Id(Long communityProfileId);



}
