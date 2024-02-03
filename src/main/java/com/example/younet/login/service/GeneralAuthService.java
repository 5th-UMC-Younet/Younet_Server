package com.example.younet.login.service;

import com.example.younet.domain.User;
import com.example.younet.domain.enums.LoginType;
import com.example.younet.domain.enums.Role;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.JwtTokenDto;
import com.example.younet.global.jwt.JwtTokenProvider;
import com.example.younet.global.jwt.OauthToken;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.login.dto.EmailVerificationDto;
import com.example.younet.login.dto.UserSigninRequestDto;
import com.example.younet.login.dto.UserSignupRequestDto;
import com.example.younet.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class GeneralAuthService {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;


    // userLoginId, password 검증 후 JWT 토큰 발급
    // redis에 refresh token 저장하기
    public JwtTokenDto signInAndGetToken(UserSigninRequestDto requestDto) {
        User user = userRepository.findByUserLoginId(requestDto.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_LOGIN));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.USER_INVALID_LOGIN);
        }

        // JWT 토큰 생성 후, refreshToken을 redis에 저장
        JwtTokenDto jwtTokenDto = tokenProvider.generateToken(user);
        redisService.setValueWithTTL(jwtTokenDto.getRefreshToken(), user.getId().toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }

    // name, email 검증 후 회원 ID 반환
    public String findId(String name, String email) {
        User user = userRepository.findByNameAndEmail(name, email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        return user.getUserLoginId();
    }

    // 아이디 검증 후 존재하는 아이디인 경우
    // 해당 메일로 인증 번호 발송
    @Async
    public void sendEmailAuthCodeForFindPassword(String userLoginId) {

        User user = userRepository.findByUserLoginId(userLoginId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));

        String email = user.getEmail();

        String authCode = mailService.generateCode();

        String body = "";
        body += "<h3>" + "안녕하세요, Younet 이메일 인증 코드입니다." + "</h3>";
        body += "<h3>" + "아래 인증코드를 입력하시면, 비밀번호를 재설정 할 수 있습니다." + "</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>" + "인증 코드는 10분간 유효합니다." + "</h3>";

        try {
            mailService.sendEmail(email, "[Younet] 비밀번호 재설정을 위한 이메일 인증", body);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }

        redisService.setValueWithTTL(email, authCode, 30L, TimeUnit.MINUTES);
    }

    // 이메일 인증 코드 검사
    public boolean verifyEmail(EmailVerificationDto emailVerificationDto) {
        String authCode = (String) redisService.getValue(emailVerificationDto.getUserEmail());

        if(!emailVerificationDto.getCode().equals(authCode)){
            throw new CustomException(ErrorCode.USER_INVALID_EMAIL_AUTH_CODE);
        }
        else {
            redisService.setValueWithTTL(emailVerificationDto.getUserEmail(), "verified", 10, TimeUnit.MINUTES);
            return true;
        }
    }

    // 비밀번호 찾기 -> 비밀번호 재설정
    public Long updatePassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_EMAIL));
        user.updatePassword(passwordEncoder.encode(password));
        return user.getId();
    }

    // 일반 자체 회원가입
    public void signUp(UserSignupRequestDto requestDto) {
        String state = (String) redisService.getValue(requestDto.getEmail());
        if(!"verified".equals(state)) throw new CustomException(ErrorCode.USER_EMAIL_AUTHENTICATION_STATUS_EXPIRED);

        User user = User.builder()
                .name(requestDto.getName())
                .nickname(requestDto.getNickname())
                .userLoginId(requestDto.getUserId())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .loginType(LoginType.INAPP)
                .role(Role.MEMBER)
                .build();

        userRepository.save(user);
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


    // 일반 로그아웃
    public void userLogout(PrincipalDetails principalDetails, String token){

        User user = principalDetails.getUser();
        user.updateRefreshToken(null);
        userRepository.save(user);

        // accessToken 유효시간을 BlackList로 저장
        Long expiration = tokenProvider.getExpiration(token);
        redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);

    }
}
