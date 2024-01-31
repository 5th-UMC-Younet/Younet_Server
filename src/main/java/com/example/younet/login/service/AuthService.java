package com.example.younet.login.service;

import com.example.younet.domain.User;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.JwtTokenDto;
import com.example.younet.global.jwt.JwtTokenProvider;
import com.example.younet.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    // 토큰 재발급
    public JwtTokenDto reissue(String oldRefreshToken) {
        String value = (String) redisService.getValue(oldRefreshToken);
        System.out.println(value);
        if ("Deprecated".equals(value) || !tokenProvider.validateToken(oldRefreshToken)) {
            throw new CustomException(ErrorCode.AUTH_DEPRECATED_REFRESH_TOKEN);
        }

        Long userId = Long.valueOf(value);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND));

        JwtTokenDto jwtTokenDto = tokenProvider.generateToken(user);
        redisService.setValueWithTTL(oldRefreshToken, "Deprecated", 7L, TimeUnit.DAYS);
        redisService.setValueWithTTL(jwtTokenDto.getRefreshToken(), userId.toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }

    public void deprecateTokens(JwtTokenDto jwtTokenDto) {
        String accessToken = jwtTokenDto.getAccessToken().replace(JwtTokenProvider.TOKEN_PREFIX, "");
        String refreshToken = jwtTokenDto.getRefreshToken();

        redisService.setValueWithTTL(accessToken, "Deprecated", 30L, TimeUnit.MINUTES);
        redisService.setValueWithTTL(refreshToken, "Deprecated", 7L, TimeUnit.DAYS);
    }


}
