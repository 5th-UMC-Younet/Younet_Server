package com.example.younet.login.service;

import com.example.younet.domain.User;
import com.example.younet.domain.enums.LoginType;
import com.example.younet.domain.enums.Role;
import com.example.younet.login.dto.EmailVerificationDto;
import com.example.younet.login.dto.UserSignupRequestDto;
import com.example.younet.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class GeneralAuthService {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    //private final JwtTokenProvider jwtTokenProvider;

    // 일반 자체 회원가입
    public void signUp(UserSignupRequestDto requestDto) {
        //String state = (String) redisService.getValue(requestDto.getUserEmail());
        //if(!"verified".equals(state)) throw new CustomException(ErrorCode.USER_EMAIL_AUTHENTICATION_STATUS_EXPIRED);

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

    // 이메일 인증 코드 검사
    public boolean verifyEmail(EmailVerificationDto emailVerificationDto) {
        String authCode = (String) redisService.getValue(emailVerificationDto.getUserEmail());

        if(!emailVerificationDto.getCode().equals(authCode))
            System.out.println("Invalid email verification code.");

        redisService.setValueWithTTL(emailVerificationDto.getUserEmail(), "verified", 10, TimeUnit.MINUTES);
        return true;
    }

    // 이메일 중복 검사
    public boolean isDuplicatedEmail(String email) {
        if(userRepository.existsByEmail(email))
            throw new DuplicateKeyException(email);
        return true;
    }

    // 이메일 인증 코드 전송
    @Async
    public void sendEmailAuthCode(String email) {

        String authCode = mailService.generateCode();
        System.out.println(authCode);

        String body = "";
        body += "<h3>" + "안녕하세요, Younet 이메일 인증 코드입니다." + "</h3>";
        body += "<h3>" + "아래 인증코드를 입력하시면, 가입이 정상 완료됩니다." + "</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>" + "인증 코드는 10분간 유효합니다." + "</h3>";

        try {
            mailService.sendEmail(email, "[Younet] 이메일 주소를 인증해주세요.", body);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }

        redisService.setValueWithTTL(email, authCode, 30L, TimeUnit.MINUTES);
    }
}
