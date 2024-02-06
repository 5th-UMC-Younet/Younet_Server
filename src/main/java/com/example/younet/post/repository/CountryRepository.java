package com.example.younet.post.repository;

import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country,Long> {
    Country findByName(String name);
}
