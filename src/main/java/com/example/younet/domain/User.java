package com.example.younet.domain;

import com.example.younet.domain.common.BaseEntity;
import com.example.younet.domain.enums.LoginType;
import com.example.younet.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String userId;

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

    private String refreshToken;

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
    public User(String userId, String password, String auth) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
