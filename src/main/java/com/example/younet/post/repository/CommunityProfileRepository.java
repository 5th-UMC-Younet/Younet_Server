package com.example.younet.post.repository;

import com.example.younet.domain.CommunityProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityProfileRepository extends JpaRepository<CommunityProfile,Long> {
}
