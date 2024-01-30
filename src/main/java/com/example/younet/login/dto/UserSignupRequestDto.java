package com.example.younet.login.dto;

import com.example.younet.domain.User;
import com.example.younet.domain.enums.LoginType;
import com.example.younet.domain.enums.Role;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupRequestDto {
    private String name;
    private String nickname;
    private String userId;
    private String email;
    private String password;
    private LoginType loginType;
    private Role role;

    @Builder
    public User toEntity() {
        return User.builder()
                .name(name)
                .nickname(nickname)
                .userLoginId(userId)
                .email(email)
                .password(password)
                .role(Role.MEMBER)
                .loginType(LoginType.INAPP)
                .build();
    }
}
