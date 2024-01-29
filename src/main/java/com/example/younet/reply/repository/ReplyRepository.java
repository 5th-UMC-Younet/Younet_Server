package com.example.younet.reply.repository;

import com.example.younet.domain.Comment;
import com.example.younet.domain.Post;
import com.example.younet.domain.Reply;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long>{
    List<Reply> findAllByPost(Post post);
    Slice<Reply> findSliceByPost_Id(Long postId, Pageable pageable);
    Slice<Reply> findSliceByComment_Id(Long commentId, Pageable pageable);
    Slice<Reply> findSliceByCommunityProfile_id(Long communityProfileId, Pageable pageable);
//    Slice<Comment> findSliceByPost(Post post, Pageable pageable);
//    Slice<Comment> findSliceByCommunityProfile(CommunityProfile communityProfile, Pageable pageable);
}