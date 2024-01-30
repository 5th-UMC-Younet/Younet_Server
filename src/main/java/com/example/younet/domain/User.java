package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.LoginType;
import com.example.younet.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint", name="user_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String userLoginId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String nickname;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(columnDefinition="TEXT")
    private String profilePicture;

    private LocalDate inactiveDate;

    private Long refreshToken;

    @Column(columnDefinition="tinyint(0) default 0")
    private boolean isDel;

    @Column(columnDefinition="tinyint(0) default 0")
    private boolean isAbroad;

    @Column(columnDefinition="tinyint(0) default 0")
    private boolean isAuth;

    @Column(length = 50)
    private String mainSkl;

    @Column(length = 50)
    private String hostContr;

    @Column(length = 50)
    private String hostSkl;

    @Builder
    public User(String userLoginId, String password, String name, String nickname, String email, Role role, LoginType loginType) {
        this.userLoginId = userLoginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.loginType = loginType;
    }

    public List<String> getRoleList(){
        if(this.role.getValue().length() > 0){
            return Arrays.asList(this.role.getValue());
        }
        return new ArrayList<>();
    }

}