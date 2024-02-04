package com.example.younet.profileauth.service;

import com.example.younet.domain.User;
import com.example.younet.global.errorException.CustomException;
import com.example.younet.global.errorException.ErrorCode;
import com.example.younet.global.jwt.PrincipalDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileAuthService {

    public void isProfileAuth(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        String isAuth = String.valueOf(user.getIsAuth());
        if(isAuth == "NOTYET"){
            throw new CustomException(ErrorCode.USER_IS_AUTH_NOTYET);
        } else if (isAuth == "PROGRESS"){
            throw new CustomException(ErrorCode.USER_IS_AUTH_PROGRESS);
        } else {
            return;
        }
    }
}
