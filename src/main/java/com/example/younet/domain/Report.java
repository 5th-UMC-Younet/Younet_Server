package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.MessageType;
import com.example.younet.domain.enums.ReportReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report extends BaseEntity { // [1:1 채팅] 메세지 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id")
    private User reporter; //신고자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id")
    private User reported; //피신고자

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason; //신고사유

    @ColumnDefault("0")
    private boolean isAccept;

    @Column(columnDefinition = "LONGTEXT")
    private String reportFile; // 신고 증명자료

}