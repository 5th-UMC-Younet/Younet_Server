package com.example.younet.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.example.younet.domain.User;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtTokenProvider {
    public static final int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 30;
    public static final int REFRESH_TOKEN_EXPRIATION_TIME = 1000 * 60 * 60 * 24 * 7;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    private final String key;
    private final UserRepository userRepository;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserRepository userRepository) {
        this.key = secretKey;
        this.userRepository = userRepository;
    }

    public JwtTokenDto generateToken(User user) {
        long now = (new Date()).getTime();

        // access token
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRATION_TIME);
        String accessToken = JWT.create()
                .withSubject(user.getEmail())
                .withClaim("id", user.getId())
                .withClaim("name", user.getNickname())
                .withExpiresAt(accessTokenExpiresIn)
                .sign(Algorithm.HMAC256(key));

        // refresh token
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPRIATION_TIME);
        String refreshToken = JWT.create()
                .withExpiresAt(refreshTokenExpiresIn)
                .sign(Algorithm.HMAC256(key));

        return JwtTokenDto.builder()
                .accessToken(TOKEN_PREFIX + accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    // authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Long userId = JWT.require(Algorithm.HMAC256(key)).build().verify(token).getClaim("id").asLong();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND)
        );

        PrincipalDetails principalDetails = new PrincipalDetails(user);

        return new UsernamePasswordAuthenticationToken(
                principalDetails,
                "",
                principalDetails.getAuthorities()
        );
    }

    public boolean validateToken(String token) {

        try {
            JWT.require(Algorithm.HMAC256(key)).build().verify(token);
            return true;
        } catch (SignatureVerificationException e) {
            throw new CustomException(ErrorCode.JWT_INVALID_TOKEN);
        } catch (TokenExpiredException e) {
            throw new CustomException(ErrorCode.JWT_EXPIRED_TOKEN);
        } catch (AlgorithmMismatchException | JWTDecodeException e) {
            throw new CustomException(ErrorCode.JWT_UNSUPPORTED_TOKEN);
        } catch (JWTVerificationException e) {
            throw new CustomException(ErrorCode.JWT_WRONG_TOKEN);
        }
    }
}