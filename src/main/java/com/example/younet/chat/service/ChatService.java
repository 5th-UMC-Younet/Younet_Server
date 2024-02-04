package com.example.younet.chat.service;

import com.example.younet.domain.ChatRequest;
import com.example.younet.domain.User;
import com.example.younet.domain.enums.Profile;
import com.example.younet.global.jwt.PrincipalDetails;

import com.example.younet.repository.ChatRequestRepository;
import com.example.younet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final UserRepository userRepository;
    private final ChatRequestRepository chatRequestRepository;


    //커뮤니티 프로필: [1:1 채팅] 요청
    @Transactional
    public ResponseEntity<?> createNicknameChatRequest(Long user_id, @AuthenticationPrincipal PrincipalDetails principalDetails){
        User requester = principalDetails.getUser(); //채팅 요청자
        User receiver = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("수락자 ID 오류" + user_id));

        // 이미 요청했는지 체크
        if(chatRequestRepository.existsByRequesterAndReceiver(requester, receiver))
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ChatRequest chatRequest = ChatRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .profile(Profile.NICKNAME)
                .build();
        chatRequestRepository.save(chatRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}