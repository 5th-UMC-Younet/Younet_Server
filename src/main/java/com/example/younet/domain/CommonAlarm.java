package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.AlarmType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

public class CommonAlarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Column(columnDefinition="tinyint(0) default 0")
    private boolean isConfirmed;

    @Column
    private Long postId;

    @Column
    private Long actorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver_id")
    private CommunityProfile receiver; // 알림 받는 사람
}
