package com.example.younet.domain;

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

public class CommunityProfile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,columnDefinition = "VARCHAR(15)")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="country_id")
    private Country country;

    @OneToMany(mappedBy = "communityProfile",cascade = CascadeType.ALL)
    private List<Post> postList=new ArrayList<>();

    @OneToMany(mappedBy = "communityProfile",cascade = CascadeType.ALL)
    private List<PostLikes> likesList=new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition="TEXT")
    private String profilePicture;
}
