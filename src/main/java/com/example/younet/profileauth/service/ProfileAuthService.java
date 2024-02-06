package com.example.younet.profileauth.service;

import com.example.younet.domain.Auth;
import com.example.younet.domain.User;
import com.example.younet.domain.enums.AuthType;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.profileauth.converter.ProfileAuthConverter;
import com.example.younet.profileauth.dto.ProfileAuthRequestDto;
import com.example.younet.repository.AuthRepository;
import com.example.younet.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileAuthService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    public int getIsProfileAuth(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        String isAuth = String.valueOf(user.getIsAuth());
        if(isAuth == "NOTYET"){
            return 0;
        } else if (isAuth == "PROGRESS"){
            return 1;
        } else {
            return 2;
        }
    }

    // 본인 인증 요청
    public void requestProfileAuth(PrincipalDetails principalDetails, ProfileAuthRequestDto profileAuthRequestDto) {
        User user = principalDetails.getUser();

        Auth auth = ProfileAuthConverter.toAuth(profileAuthRequestDto);
        auth.setImgUrl(profileAuthRequestDto.getImgUrl());
        auth.setUser(user);
        auth.setIsAuth(AuthType.PROGRESS);
        user.setIsAuth(AuthType.PROGRESS);
        user.setMainSkl(profileAuthRequestDto.getMainSchool());

        authRepository.save(auth);
        userRepository.save(user);

        return;
    }
}