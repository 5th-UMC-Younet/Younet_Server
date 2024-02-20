package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.Profile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_chatroom_id")
    private Long id;

    @Column(nullable = false,columnDefinition = "VARCHAR(25)")
    private String title; //채팅방 제목

    @Column(nullable = false,columnDefinition = "VARCHAR(50)")
    private String description; //채팅방 설명

    @Column(columnDefinition = "LONGTEXT")
    private String thumbnail; //썸네일 이미지

    @Column(columnDefinition = "VARCHAR(50)")
    private String mainSchool; //본교

    @Column(columnDefinition = "VARCHAR(50)")
    private String country; //유학국

    @Column(columnDefinition = "VARCHAR(50)")
    private String hostSchool; //파견교

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Profile profile; //실명여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user; //방장

}
