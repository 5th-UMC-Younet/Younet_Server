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
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,columnDefinition = "VARCHAR(24)")
    private String title;

    @Column(columnDefinition = "VARCHAR(50)")
    private String body;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<Image> imageList=new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communityProfile_id")
    private CommunityProfile communityProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

}
