package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Country extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,columnDefinition = "VARCHAR(15)")
    private String name;

    @OneToMany(mappedBy = "country",cascade = CascadeType.ALL)
    private List<Post> postList=new ArrayList<>();
    
    @OneToMany(mappedBy = "country",cascade = CascadeType.ALL)
    private List<CommunityProfile> profileList=new ArrayList<>();

    // 다른 엔티티들 OneToMany 추가 필요
}
