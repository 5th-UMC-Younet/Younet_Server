package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatAlarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition="tinyint(0) default 0")
    private boolean isConfirmed;

//    @Column
//    private Long requesterId; // communityProfile id가 들어와야 함

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver_id")
    private CommunityProfile receiver;

    @OneToOne
    @JoinColumn(name = "chatRequest_id")
    private ChatRequest chatRequest;

}
