package com.example.younet.comment.repository;

import com.example.younet.domain.Comment;
import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findAllByPost(Post post);
    Slice<Comment> findSliceByPost_Id(Long postId, Pageable pageable);
    Slice<Comment> findSliceByCommunityProfile_id(Long communityProfileId, Pageable pageable);
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countCommentsByPostId(@Param("postId") Long postId);
//    Slice<Comment> findSliceByPost(Post post, Pageable pageable);
//    Slice<Comment> findSliceByCommunityProfile(CommunityProfile communityProfile, Pageable pageable);
}