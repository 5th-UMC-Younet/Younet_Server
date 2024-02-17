package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.MessageType;
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
public class ChatMessage extends BaseEntity { // [1:1 채팅] 메세지 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user; //전송자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private ChatRoom chatRoom;

    @NotNull
    @Column(columnDefinition = "LONGTEXT")
    private String message; // 파일(isFile=1)인 경우, 파일 링크가 여기 들어감

    @ColumnDefault("0")
    private boolean isRead;

    @ColumnDefault("0")
    private boolean isFile; //0: 텍스트메세지, 1: 파일메세지

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private MessageType messageType; //0: 입장, 1: 전송할 메세지

}