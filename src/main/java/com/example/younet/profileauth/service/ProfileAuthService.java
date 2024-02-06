package com.example.younet.profileauth.service;

import com.example.younet.aws.AmazonS3Manager;
import com.example.younet.domain.Auth;
import com.example.younet.domain.User;
import com.example.younet.domain.enums.AuthType;
import com.example.younet.global.jwt.PrincipalDetails;
import com.example.younet.profileauth.converter.ProfileAuthConverter;
import com.example.younet.profileauth.dto.ProfileAuthRequestDto;
import com.example.younet.repository.AuthRepository;
import com.example.younet.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileAuthService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final AmazonS3Manager s3Manager;

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

    public void postProfileAuth(PrincipalDetails principalDetails, ProfileAuthRequestDto profileAuthRequestDto, MultipartFile file) {
        User user = principalDetails.getUser();

        String imageUrl = s3Manager.uploadFile(user.getId().toString(), file);
        Auth auth = ProfileAuthConverter.toAuth(profileAuthRequestDto);
        auth.setUser(user);
        auth.setIsAuth(AuthType.PROGRESS);
        auth.setImgUrl(imageUrl);
        user.setIsAuth(AuthType.PROGRESS);
        user.setMainSkl(profileAuthRequestDto.getMainSchool());

        authRepository.save(auth);
        userRepository.save(user);

        return;
    }
}