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
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,columnDefinition = "VARCHAR(15)")
    private String name;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    private List<Post> postList=new ArrayList<>();

}
