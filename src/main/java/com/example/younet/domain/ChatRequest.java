package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.Profile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest extends BaseEntity { // [1:1 채팅] 요청
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id")
    private User requester; //채팅 요청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id")
    private User receiver; //요청 수령자

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Profile profile;

    @ColumnDefault("0")
    private boolean isAccepted; //채팅 수락 여부
}