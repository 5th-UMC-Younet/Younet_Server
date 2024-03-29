package com.example.younet.domain;

import com.example.younet.comment.dto.CommentRequestDTO;
import com.example.younet.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communityProfile_id")
    private CommunityProfile communityProfile;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL)
    private List<Reply> replyList;


    public Long getPostId() {
        return post.getId();
    }

    public Long getCommunityProfileId() {
        return communityProfile.getId();
    }

    public void updateComment(CommentRequestDTO.Update dto) {
        this.body = dto.getBody();
    }

}
