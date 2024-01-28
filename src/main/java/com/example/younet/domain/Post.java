package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,columnDefinition = "VARCHAR(24)")
    private String title;

//    @Column(columnDefinition = "VARCHAR(50)")
//    private String body;

    @Column(nullable = false, columnDefinition = "BIGINT default 0")
    private Long likesCount;

//    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
//    private List<Image> imageList=new ArrayList<>();

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<PostLikes> likesList=new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Section> sections=new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityProfile communityProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    public void setCategory(Category category) {
        this.category = category;
        category.getPostList().add(this);
    }

    public void setCountry(Country country) {
        this.country = country;
        country.getPostList().add(this);
    }

    public void setCommunityProfile(CommunityProfile communityProfile) {
        this.communityProfile = communityProfile;
        communityProfile.getPostList().add(this);
    }

    public void addLike() {
        this.likesCount++;
        System.out.println(likesCount);
    }

    public void removeLike() {
        this.likesCount--;
    }
}
