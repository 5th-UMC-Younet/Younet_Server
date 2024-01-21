package com.example.younet.repository.post;

import com.example.younet.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryCustom{
}