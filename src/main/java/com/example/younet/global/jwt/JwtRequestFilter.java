package com.example.younet.global.jwt;

import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.login.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // Request Header에서 JWT 토큰 꺼내기
            String jwt = resolveToken(request);

            // 유효성검사
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Black List에 올라와 있는지 검사.
                if("Deprecated".equals(redisService.getValue(jwt))){
                    request.setAttribute("exception", ErrorCode.AUTH_DEPRECATED_ACCESS_TOKEN);
                    filterChain.doFilter(request, response);
                    return;
                }

                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else request.setAttribute("exception", ErrorCode.JWT_ABSENCE_TOKEN);

        } catch (CustomException e) {
            request.setAttribute("exception", e.getErrorCode());
        }

        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(JwtTokenProvider.HEADER_STRING);

        if (StringUtils.hasText(token) && token.startsWith(JwtTokenProvider.TOKEN_PREFIX)) {
            return token.replace(JwtTokenProvider.TOKEN_PREFIX, "");
        }
        return null;
    }

}
