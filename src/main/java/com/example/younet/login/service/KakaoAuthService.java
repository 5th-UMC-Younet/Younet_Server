package com.example.younet.login.service;

import com.example.younet.domain.User;
import com.example.younet.domain.enums.AuthType;
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
import com.google.gson.JsonElement;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class KakaoAuthService {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

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
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUri;

    public OauthToken getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";
        OauthToken oauthToken = new OauthToken();

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type="+authorizationGrantType);
            sb.append("&client_id="+clientId);
            sb.append("&redirect_uri="+redirectUri);
            sb.append("&code=" + code);
            sb.append("&client_secret="+clientSecret);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            // 토큰 확인
            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            // OauthToken 응답 객체 생성
            oauthToken.setAccess_token(access_Token);
            oauthToken.setRefresh_token(refresh_Token);

            oauthToken.setToken_type(element.getAsJsonObject().get("token_type").getAsString());
            oauthToken.setExpires_in(element.getAsJsonObject().get("expires_in").getAsInt());
            oauthToken.setScope(element.getAsJsonObject().get("scope").getAsString());
            oauthToken.setRefresh_token_expires_in(element.getAsJsonObject().get("refresh_token_expires_in").getAsInt());

            br.close();
            bw.close();
        } catch (IOException e) {
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
                    userInfoUri,
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
                    .name(profile.getKakao_account().getName())
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .email(profile.getKakao_account().getEmail())
                    .userLoginId("")
                    .password("")
                    .role(Role.MEMBER)
                    .loginType(LoginType.KAKAO)
                    .isAuth(AuthType.NOTYET)
                    .build();

            userRepository.save(user);
        }

        try {

            String serializedToken = objectMapper.writeValueAsString(oauthToken);
            redisService.setValueWithTTL(user.getId().toString(), serializedToken, 50L, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //redisService.setValueWithTTL(user.getId().toString(), oauthToken, 50L, TimeUnit.DAYS); 오류..

        JwtTokenDto jwtTokenDto = tokenProvider.generateToken(user);
        redisService.setValueWithTTL(jwtTokenDto.getRefreshToken(), user.getId().toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }

    public void kakaoLogout(PrincipalDetails principalDetails){
        User user = principalDetails.getUser();
        OauthToken oauthToken = getOauthToken(user.getId());

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

            redisService.deleteValue(user.getId().toString());
            System.out.println("삭제 완료");

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
