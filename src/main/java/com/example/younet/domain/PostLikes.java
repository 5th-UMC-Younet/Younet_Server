package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostLikes extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityProfile communityProfile;

    private boolean isActivated;

    public void activateLike() { this.isActivated = true; }
    public void deactivateLike() { this.isActivated = false; }
}
