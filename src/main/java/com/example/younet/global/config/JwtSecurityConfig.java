package com.example.younet.global.config;

import com.example.younet.login.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    //private final JwtTokenProvier jwtTokenProvier;
    private final RedisService redisService;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        //httpSecurity.addFilterBefore(new JwtRequestFilter(jwtTokenProvier, redisService), UsernamePasswordAuthenticationFilter.class);
    }

}
