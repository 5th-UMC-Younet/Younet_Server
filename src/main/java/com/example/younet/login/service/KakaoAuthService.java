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
import com.example.younet.login.dto.KakaoProfileDto;
import com.example.younet.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class KakaoAuthService {
    // 카카오 로그인 관련 로직
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String authorizationGrantType;
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String authorizationUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;

    public OauthToken getKakaoAccessToken (String code) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", authorizationGrantType);
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                authorizationUri,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oauthToken;
    }

    public KakaoProfileDto findProfile(OauthToken oauthToken) {

        String accessToken = oauthToken.getAccess_token();

        RestTemplate rt = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoProfileDto> kakaoProfileResponse = rt.exchange(
                    tokenUri,
                    HttpMethod.POST,
                    kakaoProfileRequest,
                    KakaoProfileDto.class
            );
            return kakaoProfileResponse.getBody();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.AUTH_EXPIRED_ACCESS_TOKEN);
        }

    }


    public JwtTokenDto saveUserAndGetToken(OauthToken oauthToken) {

        KakaoProfileDto profile = findProfile(oauthToken);

        User user = null;

        try {
            user = userRepository.findByEmail(profile.getKakao_account().getEmail()).get();
        } catch (NoSuchElementException e) {

            user = User.builder()
                    .name(profile.getKakao_account().getProfile().getName())
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .email(profile.getKakao_account().getEmail())
                    .userId(null)
                    .password(null)
                    .role(Role.MEMBER)
                    .loginType(LoginType.KAKAO)
                    .build();

            userRepository.save(user);
        }
        redisService.setValueWithTTL(user.getUserId().toString(), oauthToken, 50L, TimeUnit.DAYS);

        JwtTokenDto jwtTokenDto = tokenProvider.generateToken(user);
        redisService.setValueWithTTL(jwtTokenDto.getRefreshToken(), user.getUserId().toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }

    // 카카오 로그아웃
    public void serviceLogout(PrincipalDetails principalDetails){
        User user = principalDetails.getUser();

        if(user.getLoginType()!= LoginType.KAKAO) return;
        OauthToken oauthToken = getOauthToken(user.getId());
        if(oauthToken == null) return;

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        HttpEntity<MultiValueMap<String, String>> logoutRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://kapi.kakao.com/v1/user/logout",
                    HttpMethod.POST,
                    logoutRequest,
                    String.class
            );
            redisService.deleteValue(user.getUserId().toString());

        } catch (Exception e) {
            throw new CustomException(ErrorCode.AUTH_EXPIRED_ACCESS_TOKEN);
        }

    }

    // Oauth 만료 여부 확인
    public boolean isExpired(Long userId) {

        OauthToken oauthToken = (OauthToken) redisService.getValue(userId.toString());
        if (oauthToken == null) throw new CustomException(ErrorCode.AUTH_EXPIRED_OAUTH_TOKEN);

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<OauthToken> response = rt.exchange(
                    "https://kapi.kakao.com/v1/user/access_token_info",
                    HttpMethod.GET,
                    request,
                    OauthToken.class
            );
            return false;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && ex.getResponseBodyAsString().contains("-401")) {
                return true;
            } else {
                throw new CustomException(ErrorCode.AUTH_KAKAO_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    // 토큰 refresh
    public OauthToken getRefresh(Long userId) {

        OauthToken oauthToken = (OauthToken) redisService.getValue(userId.toString());
        if(oauthToken == null) throw new CustomException(ErrorCode.AUTH_EXPIRED_OAUTH_TOKEN);

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("refresh_token", oauthToken.getRefresh_token());
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> refreshRequest = new HttpEntity<>(params, headers);

        ResponseEntity<OauthToken> refreshResponse = rt.exchange(
                tokenUri,
                HttpMethod.POST,
                refreshRequest,
                OauthToken.class
        );
        OauthToken refreshOauthToken = refreshResponse.getBody();
        refreshOauthToken.setScope(oauthToken.getScope());

        if (refreshOauthToken.getRefresh_token() == null) {
            refreshOauthToken.setRefresh_token(oauthToken.getRefresh_token());
        }
        redisService.setValueWithTTL(userId.toString(), refreshOauthToken, 50L, TimeUnit.DAYS);
        return refreshOauthToken;
    }

    public OauthToken getOauthToken(Long userId) {
        OauthToken oauthToken;
        if (isExpired(userId)) {
            oauthToken = getRefresh(userId);
        }else {
            oauthToken = (OauthToken) redisService.getValue(userId.toString());
        }
        return oauthToken;
    }
}
