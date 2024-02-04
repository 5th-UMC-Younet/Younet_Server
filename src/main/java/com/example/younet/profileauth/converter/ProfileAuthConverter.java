package com.example.younet.profileauth.converter;

import com.example.younet.domain.Auth;
import com.example.younet.domain.Post;
import com.example.younet.domain.enums.AuthType;
import com.example.younet.post.dto.PostRequestDTO;
import com.example.younet.profileauth.dto.ProfileAuthRequestDto;

import java.util.ArrayList;

public class ProfileAuthConverter {

    public static Auth toAuth(ProfileAuthRequestDto requestDto) {
        return Auth.builder()
                .imgUrl(requestDto.getImgUrl())
                .isAuth(AuthType.PROGRESS)
                .build();
    }
}
