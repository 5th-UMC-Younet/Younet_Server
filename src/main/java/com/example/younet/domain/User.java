package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.AuthType;
import com.example.younet.domain.enums.LoginType;
import com.example.younet.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "is_del = false")
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

    @Column(length = 255)
    private String profileText;

    private LocalDate inactiveDate;

    private Long refreshToken;

    @Column(columnDefinition="tinyint(0) default 0")
    private boolean isDel;

    @Column(columnDefinition="tinyint(0) default 0")
    private boolean isAbroad;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthType isAuth;

    @Column(length = 50)
    private String mainSkl;

    @Column(length = 50)
    private String hostContr;

    @Column(length = 50)
    private String hostSkl;

    public void updateRefreshToken(Long refreshToken){
        this.refreshToken = refreshToken;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @Builder
    public User(Long userId, String userLoginId, String password, String name, String nickname, String email, Role role, LoginType loginType, AuthType authType) {
        this.id = userId;
        this.userLoginId = userLoginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.loginType = loginType;
        this.isAuth = authType;
    }

    public List<String> getRoleList(){
        if(this.role.getValue().length() > 0){
            return Arrays.asList(this.role.getValue());
        }
        return new ArrayList<>();
    }

}
