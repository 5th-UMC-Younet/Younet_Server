package com.example.younet.login.service;

import com.example.younet.domain.User;
import com.example.younet.domain.enums.AuthType;
import com.example.younet.domain.enums.LoginType;
import com.example.younet.domain.enums.Role;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.JwtTokenDto;
import com.example.younet.global.jwt.JwtTokenProvider;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.login.dto.*;
import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class GeneralAuthService {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;


    public JwtTokenDto postGeneralSignIn(UserSigninRequestDto requestDto) {
        User user = userRepository.findByUserLoginId(requestDto.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_LOGIN));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.USER_INVALID_LOGIN);
        }

        JwtTokenDto jwtTokenDto = tokenProvider.generateToken(user);
        redisService.setValueWithTTL(jwtTokenDto.getRefreshToken(), user.getId().toString(), 7L, TimeUnit.DAYS);
        return jwtTokenDto;
    }

    public String getFindId(FindIdRequestDto findIdRequestDto) {
        User user = userRepository.findByNameAndEmail(findIdRequestDto.getName(), findIdRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        return user.getUserLoginId();
    }

    @Async
    public void sendEmailAuthCodeForFindPassword(String userLoginId) {

        User user = userRepository.findByUserLoginId(userLoginId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        String email = user.getEmail();
        String loginId = user.getUserLoginId();
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
        redisService.setValueWithTTL(loginId, authCode, 30L, TimeUnit.MINUTES);
    }

    public FindPasswordEmailVerificationResponseDto postPasswordVerifyEmail(FindPasswordEmailVerficationRequestDto findPasswordEmailVerficationRequestDto) {
        String authCode = (String) redisService.getValue(findPasswordEmailVerficationRequestDto.getLoginId());

        if(!findPasswordEmailVerficationRequestDto.getCode().equals(authCode)){
            throw new CustomException(ErrorCode.USER_INVALID_EMAIL_AUTH_CODE);
        }
        else {
            redisService.setValueWithTTL(findPasswordEmailVerficationRequestDto.getLoginId(), "verified", 10, TimeUnit.MINUTES);
            FindPasswordEmailVerificationResponseDto findPasswordEmailVerificationResponseDto = new FindPasswordEmailVerificationResponseDto();
            findPasswordEmailVerificationResponseDto.setLoginId(findPasswordEmailVerficationRequestDto.getLoginId());
            return findPasswordEmailVerificationResponseDto;
        }
    }

    public void postResetPassword(NewPasswordRequestDto newPasswordRequestDto) {
        User user = userRepository.findByUserLoginId(newPasswordRequestDto.getLoginId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_INVALID_FIND_ID));
        user.updatePassword(passwordEncoder.encode(newPasswordRequestDto.getNewPassword()));
        return;
    }

    public void postGeneralSignUp(UserSignupRequestDto requestDto) {
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
                .isAuth(AuthType.NOTYET)
                .build();
        userRepository.save(user);
    }

    public boolean isDuplicatedEmail(String email) {
        if(userRepository.existsByEmail(email))
            throw new DuplicateKeyException(email);
        return true;
    }

    @Async
    public void sendEmailAuthCode(String email) {
        String authCode = mailService.generateCode();
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

    public boolean signupVerifyEmail(SignupEmailVerificationDto emailVerificationDto) {
        String authCode = (String) redisService.getValue(emailVerificationDto.getUserEmail());
        if(!emailVerificationDto.getCode().equals(authCode)){
            throw new CustomException(ErrorCode.USER_INVALID_EMAIL_AUTH_CODE);
        }
        else {
            redisService.setValueWithTTL(emailVerificationDto.getUserEmail(), "verified", 10, TimeUnit.MINUTES);
            return true;
        }
    }

    public void postUserLogout(PrincipalDetails principalDetails, String token){

        User user = principalDetails.getUser();
        user.updateRefreshToken(null);
        userRepository.save(user);
        Long expiration = tokenProvider.getExpiration(token);
        redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);

    }

    public void patchUserWithdrawl(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        user.setDel(true);
        user.setInactiveDate(LocalDate.from(LocalDateTime.now()));
        userRepository.save(user);
    }
}
