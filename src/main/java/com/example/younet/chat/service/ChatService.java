package com.example.younet.chat.service;

import com.example.younet.chat.dto.*;
import com.example.younet.domain.*;
import com.example.younet.domain.enums.Profile;
import com.example.younet.domain.enums.ReportReason;
import com.example.younet.global.jwt.PrincipalDetails;

import com.example.younet.post.repository.CommunityProfileRepository;
import com.example.younet.repository.*;
import com.example.younet.userprofile.profile.dto.UserProfileDto;
import com.example.younet.userprofile.profile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final UserRepository userRepository;
    private final ChatRequestRepository chatRequestRepository;
    private final JoinChatRepository joinChatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CommunityProfileRepository communityProfileRepository;
    private final MessageRepository messageRepository;
    private final ChatAlarmRepository chatAlarmRepository;
    private final OpenChatRoomRepository openChatRoomRepository;
    private final UserProfileService userProfileService;
    private final JoinOpenChatRepository joinOpenChatRepository;
    private final ReportRepository reportRepository;
    private final OpenMessageRepository openMessageRepository;

    //커뮤니티 프로필: [1:1 채팅] 요청
    @Transactional
    public ResponseEntity<?> createNicknameChatRequest(Long user_id, @AuthenticationPrincipal PrincipalDetails principalDetails){
        User requester = principalDetails.getUser(); //채팅 요청자
        User receiver = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("수락자 ID 오류" + user_id));

        // 이미 요청했는지 체크
        if(chatRequestRepository.existsByRequesterAndReceiverAndProfile(requester, receiver, Profile.NICKNAME))
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ChatRequest chatRequest = ChatRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .profile(Profile.NICKNAME)
                .build();
//        chatRequestRepository.save(chatRequest);

        chatAlarmRepository.save(
                ChatAlarm.builder()
                        .isConfirmed(false)
//                        .requesterId(communityProfileRepository.findByUserId(requester.getId()).getId())
//                        .receiver(communityProfileRepository.findByUserId(receiver.getId()))
                        .chatRequest(chatRequestRepository.save(chatRequest))
                        .build()
        );

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //참여중인 1:1 채팅 목록
    @Transactional
    public List<OneToOneChatListDto> readOneToOneChatList(@AuthenticationPrincipal PrincipalDetails principalDetails){
        User loginUser = principalDetails.getUser(); //현재 로그인된 유저

        //로직: JoinChat 테이블에서 (회원ID = 현재 로그인된 유저 객체의 ID) 인 리스트 조회
        List<JoinChat> joinChatList = joinChatRepository.findByUserId(loginUser.getId());

        List<OneToOneChatListDto> result = new ArrayList<>();

        for (int i=0; i<joinChatList.size(); i++)
        {
            // 조회한 JoinChat 리스트에 나온 채팅방 ID를 사용하여 채팅방 목록 중복없이 조회 (조회시 메세지 최신순 정렬)
            List<ChatRoom> chatRoomList = chatRoomRepository.findByChatroomIdOrderByMessageCreatedAtDesc(joinChatList.get(i).getChatRoom().getId());

            User otheruser = joinChatRepository.findJoinChatByAnotherUser(chatRoomList.get(i).getId(), loginUser.getId()).getUser();
            String name, img, message;
            LocalDateTime createdAt;
            Long chatRoomId = chatRoomList.get(i).getId();

            Message lastestMessage = messageRepository.findLatestMessage(chatRoomList.get(i).getId());
            message = lastestMessage.getMessage(); // 가장 최근 메세지 내용
            createdAt = lastestMessage.getCreatedAt(); // 메세지 생성 시각(created_at)

            if (chatRoomList.get(i).getProfile() == Profile.REALNAME) { // 실명 프로필
                name = otheruser.getName();
                img = otheruser.getProfilePicture();
            } else { // 닉네임 프로필
                CommunityProfile communityProfile = communityProfileRepository.findByUserId(otheruser.getId());
                name = communityProfile.getName(); // 닉네임
                img = communityProfile.getProfilePicture();
            }
            result.add(OneToOneChatListDto.builder()
                    .chatRoomId(chatRoomId)
                    .name(name)
                    .profilePicture(img)
                    .message(message)
                    .createdAt(createdAt)
                    .build());
        }
            //TODO: 안읽은 메세지 수 카운트하는 로직 추가

        return result;
    }


    //채팅방 메세지 불러오기 (1:1)
    @Transactional
    public ReadAllMessageDto readAllMessages(Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        User loginUser = principalDetails.getUser(); //현재 로그인된 유저 객체
        List<MessageListDto> messageListDtos = messageRepository.findMessagesByChatRoomId(chat_room_id)
                .stream().map(MessageListDto::new).toList();

        ReadAllMessageDto allMessageDto = ReadAllMessageDto.builder()
                .loginUserId(loginUser.getId())
                .messageListDtoList(messageListDtos)
                .build();
        return allMessageDto;
    }

    //[1:1 채팅] 요청 수락 및 채팅방 생성 API
    @Transactional
    public ResponseEntity<?> acceptChatRequest(Long chatAlarmId, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        User loginUser = principalDetails.getUser();
        // 1. chatAlarm: isConfirmed 필드 true
        ChatAlarm chatAlarm=chatAlarmRepository.findById(chatAlarmId).get();
        chatAlarm.setConfirmed(true);

        ChatRequest chatRequest=chatRequestRepository.findById(chatAlarm.getChatRequest().getId()).get();
        chatRequest.setAccepted(true);

        // 2. ChatRoom 및 JoinChat 데이터 추가
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .profile(chatAlarm.getChatRequest().getProfile())
                .build());

        joinChatRepository.save(JoinChat.builder()
                .chatRoom(chatRoom)
                .user(loginUser) //로그인한 사용자 -> 수락자 = 요청받은자(Receiver)
                .build());

        joinChatRepository.save(JoinChat.builder()
                .chatRoom(chatRoom)
                .user(chatAlarm.getChatRequest().getRequester()) //요청자(requester)
                .build());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //[1:1 채팅]에서 유저 프로필 조회하는 API
    @Transactional
    public ResponseEntity<?> getOneToOneUserProfile(Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chat_room_id);
        User loginUser = principalDetails.getUser(); //로그인된 유저
        User otherUser = joinChatRepository.findJoinChatByAnotherUser(chat_room_id, loginUser.getId()).getUser(); //1:1 채팅 상대방

        if (chatRoom.get().getProfile() == Profile.NICKNAME)
        {
            //커뮤니티 프로필 [1:1 채팅]인 경우
            UserProfileDto.UserResultDTO userResultDTO = userProfileService.getUserProfileInfo(otherUser.getId());
            return ResponseEntity.ok(userResultDTO);
        }

        //실명 프로필 [1:1 채팅]인 경우
        return ResponseEntity.ok(getUserRealNameProfile(otherUser.getId()));
    }

    //실명프로필 조회 API (함수화)
    @Transactional
    public ResponseEntity<?> getUserRealNameProfile(Long user_id)
    {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("user_id 오류" + user_id));

        UserRealProfileDto userRealProfileDto = UserRealProfileDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .profilePicture(user.getProfilePicture())
                .mainSkl(user.getMainSkl())
                .hostContr(user.getHostContr())
                .hostSkl(user.getHostSkl())
                .profileText(user.getProfileText())
                .build();
        return ResponseEntity.ok(userRealProfileDto);
    }

    //오픈 채팅방: [1:1 채팅] 요청 -> 닉네임채팅방 / 실명채팅방
    @Transactional
    public ResponseEntity<?> createOpenChatRequest(Long open_chatroom_id, Long user_id, @AuthenticationPrincipal PrincipalDetails principalDetails){
        User requester = principalDetails.getUser(); //채팅 요청자
        User receiver = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("수락자 ID 오류" + user_id));

        Optional<OpenChatRoom> openChatRoom = openChatRoomRepository.findById(open_chatroom_id);

        if (openChatRoom.get().getProfile() == Profile.NICKNAME) //닉네임 채팅방인 경우
        {
            return ResponseEntity.ok(createNicknameChatRequest(user_id, principalDetails));
        }
        else //실명프로필로 요청하는 경우
        {
            //이미 요청이 있는지 확인
            if(chatRequestRepository.existsByRequesterAndReceiverAndProfile(requester, receiver, Profile.REALNAME))
            {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            //요청이 없다면
            ChatRequest chatRequest = ChatRequest.builder()
                    .requester(requester)
                    .receiver(receiver)
                    .profile(Profile.REALNAME) //실명프로필로 요청
                    .build();

            chatAlarmRepository.save(
                    ChatAlarm.builder()
                            .isConfirmed(false)
                            .chatRequest(chatRequestRepository.save(chatRequest))
                            .build()
            );
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    //오픈채팅방 상세정보 및 유저참여가능여부 GET
    @Transactional
    public OpenChatRoomDetailDto getOpenChatRoomInfo(Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        boolean isJoin = true;
        Optional<OpenChatRoom> openChatRoom = openChatRoomRepository.findById(chat_room_id);

        if (openChatRoom.get().getMainSchool() != null) //본교가 상관없음이 아닐 경우,
        {
            if (openChatRoom.get().getMainSchool() == principalDetails.getUser().getMainSkl()) //본교가 같은 경우
            {
                if (openChatRoom.get().getCountry() != null) //유학국이 상관없음이 아닐 경우
                {
                    if (openChatRoom.get().getCountry() == principalDetails.getUser().getHostContr()) //유학국이 같은 경우
                    {
                        if(openChatRoom.get().getHostSchool() != null && openChatRoom.get().getHostSchool() != principalDetails.getUser().getHostSkl())
                        //파견교가 상관없음이 아닌데, 파견교가 다를 경우
                        {
                            isJoin = false;
                        }
                    }
                    else //유학국이 다를 경우
                    {
                        isJoin = false;
                    }
                }
            } else //본교가 같지 않은 경우
            {
                isJoin = false;
            }
        }

        OpenChatRoomDetailDto openChatRoomDetailDto = OpenChatRoomDetailDto.builder()
                .openChatRoomId(openChatRoom.get().getId())
                .profile(openChatRoom.get().getProfile().toString())
                .thumbnail(openChatRoom.get().getThumbnail())
                .title(openChatRoom.get().getTitle())
                .description(openChatRoom.get().getDescription())
                .mainSkl(openChatRoom.get().getMainSchool())
                .hostContr(openChatRoom.get().getCountry())
                .hostSkl(openChatRoom.get().getHostSchool())
                .userId(principalDetails.getUser().getId())
                .isJoin(isJoin)
                .build();

        return openChatRoomDetailDto;
    }

    //참여 버튼 눌렀을때: 오픈채팅방 입장(참여) API
    @Transactional
    public ResponseEntity<?> joinOpenChatRoom(Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        OpenChatRoom openChatRoom = openChatRoomRepository.findById(chat_room_id)
                .orElseThrow(() -> new IllegalArgumentException("오픈 채팅방 id 오류" + chat_room_id));
        JoinOpenChat joinOpenChat = JoinOpenChat.builder()
                .openChatRoom(openChatRoom)
                .user(principalDetails.getUser())
                .build();
        joinOpenChatRepository.save(joinOpenChat);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}