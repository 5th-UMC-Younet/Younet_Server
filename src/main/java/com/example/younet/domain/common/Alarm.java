package com.example.younet.domain.common;

import com.example.younet.domain.CommunityProfile;
import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.AlarmType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Alarm {

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityProfile receiverCommunityProfile;

    @Column(nullable = true)
    private String message;

    @Column(nullable = false)
    private AlarmType type;

    @Column(nullable = false)
    private boolean isChecked;

    @CreatedDate
    private LocalDateTime createdAt;

    public abstract Long getTargetEntityId();
}
