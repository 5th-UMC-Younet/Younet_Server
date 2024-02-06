package com.example.younet.login.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.younet.global.dto.ApplicationResponse;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.*;
import com.example.younet.global.jwt.OauthToken;
import com.example.younet.login.dto.*;
import com.example.younet.login.service.AuthService;
import com.example.younet.login.service.GeneralAuthService;
import com.example.younet.login.service.KakaoAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class UserAuthController {

    private final GeneralAuthService generalAuthService;
    private final AuthService authService;
    private final KakaoAuthService kakaoAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    // 일반 로그인
    @PostMapping("/user/login")
    public ApplicationResponse<JwtTokenDto> generalSignIn(@RequestBody UserSigninRequestDto requestDto) {
        JwtTokenDto jwtTokenDto = generalAuthService.postGeneralSignIn(requestDto);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    // 아이디 찾기
    @GetMapping("/user/findId")
    public ApplicationResponse<String> findId(@RequestBody FindIdRequestDto findIdRequestDto) {
        String findUserId = generalAuthService.getFindId(findIdRequestDto);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, findUserId);
    }

    // 비밀번호 찾기 - 이메일 인증번호 전송
    @PostMapping("/user/findPassword/email")
    public ResponseEntity<String> getEmailAuthCodeForFindPassword(@RequestParam(name = "loginId") String userLoginId) {
        generalAuthService.sendEmailAuthCodeForFindPassword(userLoginId);
        return ResponseEntity.ok("가입 시 입력하신 이메일로 인증 번호가 발송되었습니다.");
    }

    // 비밀번호 찾기 - 이메일 인증 성공 여부 확인
    @PostMapping("/user/findPassword/email/verification")
    public ApplicationResponse<FindPasswordEmailVerificationResponseDto> passwordVerifyEmail(@RequestBody FindPasswordEmailVerficationRequestDto findPasswordEmailVerficationRequestDto) {
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, generalAuthService.postPasswordVerifyEmail(findPasswordEmailVerficationRequestDto));
    }

    // 비밀번호 재설정
    @PostMapping("/user/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody NewPasswordRequestDto newPasswordRequestDto) {
        generalAuthService.postResetPassword(newPasswordRequestDto);
        return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
    }

    // 회원가입 - 이메일 인증 코드 전송
    @PostMapping("/user/signup/email")
    public ResponseEntity<String> getEmailAuthCodeForSignUp(@RequestParam(name = "email") String email) {
        if (generalAuthService.isDuplicatedEmail(email)) {
            generalAuthService.sendEmailAuthCode(email);
            return ResponseEntity.ok("입력하신 이메일로 인증 번호가 발송되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 이메일입니다.");
        }
    }

    // 회원가입 - 이메일 인증 성공 여부 확인
    @PostMapping("/user/signup/email/verification")
    public ResponseEntity<String> signUpVerifyEmail(@RequestBody SignupEmailVerificationDto signupEmailVerificationDto) {
        boolean verificationResult = generalAuthService.signupVerifyEmail(signupEmailVerificationDto);
        if (verificationResult) {
            return ResponseEntity.ok("인증에 성공하였습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증에 실패하였습니다.");
        }
    }

    // 일반 회원가입
    @PostMapping("/user/signup")
    public ApplicationResponse<String> generalSignUp(@RequestBody UserSignupRequestDto requestDto) {
        if(generalAuthService.isDuplicatedEmail(requestDto.getEmail())) {
            generalAuthService.postGeneralSignUp(requestDto);
        }
        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, "회원가입이 완료되었습니다.");
    }

    // 일반 로그아웃
    @PostMapping("/user/logout")
    public ApplicationResponse<String> userLogout(HttpServletRequest request, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String token = jwtTokenProvider.resolveAccessToken(request);
        generalAuthService.postUserLogout(principalDetails, token);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "일반 로그아웃 되었습니다.");
    }

    // 일반 회원탈퇴
    @PatchMapping("/user/withdrawl")
    public ApplicationResponse<String> userWithdrawl(HttpServletRequest request, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        generalAuthService.patchUserWithdrawl(principalDetails);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "회원 탈퇴가 완료되었습니다.");
    }

    // 카카오 로그인
    @GetMapping("/oauth2/kakao")
    public ApplicationResponse<JwtTokenDto> Login(@RequestParam("code") String code) {
        OauthToken oauthToken = kakaoAuthService.getKakaoAccessToken(code);
        JwtTokenDto jwtTokenDto = kakaoAuthService.saveUserAndGetToken(oauthToken);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, jwtTokenDto);
    }

    // 카카오 로그아웃
    @PostMapping("/kakao/logout")
    public ApplicationResponse<String> serviceLogout(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody JwtTokenDto jwtTokenDto) throws JsonProcessingException {
        authService.deprecateTokens(jwtTokenDto);
        kakaoAuthService.kakaoLogout(principalDetails);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "카카오 로그아웃 되었습니다.");
    }

    // 토큰 재발급
    @PostMapping("/auth/reissue")
    public ApplicationResponse<JwtTokenDto> reissue(@RequestBody ReissueRequestDto reissueRequestDto) {
        JwtTokenDto jwtTokenDto = authService.reissue(reissueRequestDto.getRefreshToken());
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    // JWT 토큰 디코딩 (json 형식 변환)
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
            String json = "{ \"name\": \"" + name + "\", \"nickname\": \"" + nickname + "\", \"email\": \"" + email + "\"}";
            return json;
        } catch (Exception e) {
            return "{ \"error\": \"Token invalid\" }";
        }
    }
}
