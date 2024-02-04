package com.example.younet.domain;

import com.example.younet.domain.enums.AuthType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthType isAuth;

    @Column(columnDefinition="TEXT")
    private String imgUrl;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "mainSchool_id")
    private MainSchool mainSchool;
}
