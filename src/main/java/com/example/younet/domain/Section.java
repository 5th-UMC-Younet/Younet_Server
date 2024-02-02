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
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id", referencedColumnName = "post_id")
    private Post post;

    @OneToMany(mappedBy = "section",cascade = CascadeType.ALL)
    private List<Image> images= new ArrayList<>();
}