package com.example.younet.post.repository;

import com.example.younet.domain.CommunityProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityProfileRepository extends JpaRepository<CommunityProfile,Long> {
    Optional<CommunityProfile> findById(Long userId);

    CommunityProfile findByUserId(Long userId);
}
