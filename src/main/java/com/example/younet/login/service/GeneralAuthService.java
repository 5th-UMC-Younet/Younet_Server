package com.example.younet.login.service;

import com.example.younet.domain.User;
import com.example.younet.domain.enums.LoginType;
import com.example.younet.domain.enums.Role;
import com.example.younet.login.dto.UserSignupRequestDto;
import com.example.younet.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class GeneralAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 일반 자체 회원가입
    public void signUp(UserSignupRequestDto requestDto) {

        User user = User.builder()
                .name(requestDto.getName())
                .nickname(requestDto.getNickname())
                .userId(requestDto.getUserId())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .loginType(LoginType.INAPP)
                .role(Role.MEMBER)
                .build();

        userRepository.save(user);
    }
}
