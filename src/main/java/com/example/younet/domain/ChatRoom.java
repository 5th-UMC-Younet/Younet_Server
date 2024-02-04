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
public class ChatRoom extends BaseEntity { //1:1 채팅방 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Profile profile;

}
