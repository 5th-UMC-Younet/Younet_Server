package com.example.younet.domain.alarm;

import com.example.younet.domain.Post;
import com.example.younet.domain.common.Alarm;
import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.AlarmType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PostCommentAlarm extends Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long temp;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Post targetPost;

    @Override
    public Long getTargetEntityId() {
        return targetPost.getId();
    }
}
