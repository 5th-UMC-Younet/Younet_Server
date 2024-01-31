package com.example.younet.login.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.younet.global.dto.ApplicationResponse;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.JwtTokenDto;
import com.example.younet.global.jwt.OauthToken;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.login.dto.EmailVerificationDto;
import com.example.younet.login.dto.ReissueRequestDto;
import com.example.younet.login.dto.UserSigninRequestDto;
import com.example.younet.login.dto.UserSignupRequestDto;
import com.example.younet.login.service.AuthService;
import com.example.younet.login.service.GeneralAuthService;
import com.example.younet.login.service.KakaoAuthService;
import com.example.younet.login.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;
    private final GeneralAuthService generalAuthService;
    private final AuthService authService;
    private final KakaoAuthService kakaoAuthService;


    // 일반 회원가입
    @PostMapping("/user/signup")
    public ApplicationResponse<String> generalSignUp(@RequestBody UserSignupRequestDto requestDto) {
        if(generalAuthService.isDuplicatedEmail(requestDto.getEmail())) {
            generalAuthService.signUp(requestDto);
        }
        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, "회원가입 성공");
    }

    // 이메일 인증 코드 전송
    @PostMapping("/signup/email")
    public ResponseEntity<String> getEmailAuthCode(@RequestParam(name = "email") String email) {
        if (generalAuthService.isDuplicatedEmail(email)) {
            generalAuthService.sendEmailAuthCode(email);
            return ResponseEntity.ok("인증 코드가 전송되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 이메일입니다.");
        }
    }

    // 이메일 인증 성공 여부 확인
    @PostMapping("/signup/verification/email")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailVerificationDto emailVerificationDto) {
        boolean verificationResult = generalAuthService.verifyEmail(emailVerificationDto);
        if (verificationResult) {
            return ResponseEntity.ok("인증에 성공하였습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증에 실패하였습니다.");
        }
    }

    // 일반 로그인
    @PostMapping("/user/login")
    public ApplicationResponse<JwtTokenDto> generalSignIn(@RequestBody UserSigninRequestDto requestDto) {
        JwtTokenDto jwtTokenDto = generalAuthService.signInAndGetToken(requestDto);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    // 토큰 재발급
    @PostMapping("/auth/reissue")
    public ApplicationResponse<JwtTokenDto> reissue(@RequestBody ReissueRequestDto reissueRequestDto) {
        JwtTokenDto jwtTokenDto = authService.reissue(reissueRequestDto.getRefreshToken());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    // 카카오 로그인 - OauthToken 발급 후 회원 정보 DB저장/JWT생성
    @GetMapping("/oauth2/kakao")
    public ApplicationResponse<JwtTokenDto> Login(@RequestParam("code") String code) {

        OauthToken oauthToken = kakaoAuthService.getKakaoAccessToken(code);
        JwtTokenDto jwtTokenDto = kakaoAuthService.saveUserAndGetToken(oauthToken);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, jwtTokenDto);
    }

    // JWT 토큰 디코딩 -> json 형식으로 반환
    @PostMapping("/decodeToken")
    public String decodeToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "{ \"error\": \"Bearer token missing\" }";
        }
        String token = authHeader.substring(7); // Bearer 부분 제외
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String name = decodedJWT.getClaim("name").asString();
            String nickname = decodedJWT.getClaim("nickname").asString();
            String email = decodedJWT.getClaim("sub").asString();
            String json = "{ \"name\": \"" + name + "\", \"username\": \"" + nickname + "\", \"email\": \"" + email + "\"}";
            return json;
        } catch (Exception e) {
            return "{ \"error\": \"Token invalid\" }";
        }
    }

    // 로그아웃 (JwtToken, OauthToken 만료하기)
    @PostMapping("/auth/logout")
    public ApplicationResponse<String> serviceLogout(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @RequestBody JwtTokenDto jwtTokenDto) {
        authService.deprecateTokens(jwtTokenDto);
        kakaoAuthService.serviceLogout(principalDetails);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "로그아웃 되었습니다.");
    }
}
