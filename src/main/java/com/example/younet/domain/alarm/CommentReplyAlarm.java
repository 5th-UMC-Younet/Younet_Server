package com.example.younet.domain.alarm;

import com.example.younet.domain.Comment;
import com.example.younet.domain.Post;
import com.example.younet.domain.common.Alarm;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CommentReplyAlarm extends Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Comment targetComment;

    @Override
    public Long getTargetEntityId() {
        return targetComment.getId();
    }

}
