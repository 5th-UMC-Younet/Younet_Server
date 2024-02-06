package com.example.younet.repository;

import com.example.younet.domain.MainSchool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainSchoolRepository extends JpaRepository<MainSchool, Long> {
    MainSchool findByName(String name);
    boolean existsByName(String name);
}
