package com.example.younet.global.errorException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 성공 (1000번대)
    SUCCESS_OK(HttpStatus.OK, "성공", 1000),
    SUCCESS_CREATED(HttpStatus.CREATED, "생성 성공", 1001),

    // JWT, Auth (2000번대)
    JWT_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.", 2001),
    JWT_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.", 2002),
    JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다.", 2003),
    JWT_WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 JWT 토큰 입니다.", 2004),
    JWT_ABSENCE_TOKEN(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.", 2005),
    AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다.", 2006),
    AUTH_KAKAO_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "카카오 서버가 일시적 내부 장애상태 입니다", 2007),
    AUTH_EXPIRED_OAUTH_TOKEN(HttpStatus.BAD_REQUEST, "카카오로부터 정보를 받아올 수 없습니다. 다시 로그인해주세요.", 2008),
    AUTH_EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 엑세스 토큰입니다.", 2009),
    AUTH_BAD_LOGOUT_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다", 20010),
    AUTH_DEPRECATED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "만료된 Refresh 토큰입니다.", 2011),
    AUTH_DEPRECATED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "더 이상 사용되지 않는 Access 토큰입니다", 2012),

    // User (3000번대)
    USER_INVALID_LOGIN(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸거나 존재하지 않는 아이디입니다.", 3001),
    USER_INVALID_FIND_ID(HttpStatus.BAD_REQUEST, "관련된 아이디가 존재하지 않습니다.", 3002),
    USER_INVALID_EMAIL_AUTH_CODE(HttpStatus.BAD_REQUEST, "유효하지 않는 인증 코드입니다.", 3003),
    USER_INVALID_FIND_EMAIL(HttpStatus.BAD_REQUEST, "관련된 이메일이 존재하지 않습니다.", 3004),
    USER_INVALID_LOGIN_TYPE(HttpStatus.BAD_REQUEST, "잘못된 로그인 타입입니다.", 3005),
    USER_DUPLICATED_USER_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.", 3006),
    USER_EMAIL_AUTHENTICATION_STATUS_EXPIRED(HttpStatus.NOT_FOUND, "이메일 인증을 다시 해주세요.", 3007),

    // Redis 관련
    REDIS_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다.", 7001),

    // 알 수 없는 에러
    UNKNOWN_ERROR(HttpStatus.BAD_GATEWAY, "알 수 없는 오류입니다",8001),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private final int code;
}