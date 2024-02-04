package com.example.younet.profileauth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileAuthService {



    // 서비스 리턴 값에 따라 반환 값 다르게?
    // NOTYET 이면 "본인 인증 전에는 이용할 수 없습니다." -> 서비스 단에서 처리
    // PROGRESSTING 이면 "본인 인증이 진행 중입니다." -> 서비스 단에서 처리
    // DONE 이면 ok 던지기
}
