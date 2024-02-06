package com.example.younet.profileauth.converter;

import com.example.younet.domain.Auth;
import com.example.younet.domain.enums.AuthType;
import com.example.younet.profileauth.dto.ProfileAuthRequestDto;
import org.springframework.web.multipart.MultipartFile;

public class ProfileAuthConverter {

    public static Auth toAuth(ProfileAuthRequestDto requestDto) {
        return Auth.builder()
                .isAuth(AuthType.PROGRESS)
                .build();
    }
}
