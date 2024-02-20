package com.example.younet.chat.service;

import com.example.younet.chat.dto.*;
import com.example.younet.domain.*;
import com.example.younet.domain.enums.MessageType;
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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.younet.domain.QChatRoom.chatRoom;

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
                        .receiver(communityProfileRepository.findByUserId(receiver.getId()))
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

        //TODO: JoinChat 리스트에 나온 채팅방 ID를 사용하여 채팅방 목록 중복없이 조회 (조회시 메세지 최신순 정렬)

        List<OneToOneChatListDto> result = new ArrayList<>();

        if (!joinChatList.isEmpty()) {
            for (int i = 0; i < joinChatList.size(); i++) {
                JoinChat joinChat = joinChatList.get(i);
                if (joinChat != null && joinChat.getChatRoom() != null) {
                    ChatRoom chatRoom = chatRoomRepository.findById(joinChat.getChatRoom().getId())
                            .orElseThrow(() -> new IllegalArgumentException("chatroom_id를 찾을 수 없습니다."));

                User otheruser = joinChatRepository.findJoinChatByAnotherUser(chatRoom.getId(), loginUser.getId()).getUser();
                String name, img, message;
                LocalDateTime createdAt;
                Long chatRoomId = chatRoom.getId();
                boolean profile = false;

                ChatMessage lastestChatMessage = messageRepository.findLatestMessage(chatRoomId);

                if (lastestChatMessage == null) {
                    message = null;
                    createdAt = null;
                } else {
                    message = lastestChatMessage.getMessage(); // 가장 최근 메세지 내용
                    createdAt = lastestChatMessage.getCreatedAt(); // 메세지 생성 시각(created_at)
                }

                if (chatRoom.getProfile() == Profile.REALNAME) { // 실명 프로필
                    name = otheruser.getName();
                    img = otheruser.getProfilePicture();
                    profile = true;
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
                        .profile(profile)
                        .build());
                }
            }
            //TODO: 안읽은 메세지 수 카운트하는 로직 추가
        }

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

    //채팅방 메세지 불러오기 (오픈채팅)
    @Transactional
    public ReadAllOpenMessageDto readAllOpenMessages(Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        User loginUser = principalDetails.getUser(); //현재 로그인된 유저 객체
        List<OpenMessageListDto> messageListDtos = openMessageRepository.findById(chat_room_id)
                .stream().map(OpenMessageListDto::new).toList();

        ReadAllOpenMessageDto allMessageDto = ReadAllOpenMessageDto.builder()
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

        List<JoinOpenChat> joinOpenChats = joinOpenChatRepository.findJoinOpenChatsById(openChatRoom.get().getId());

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
                .participants(joinOpenChats.size())
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
        //TODO: 이미 참여중인 경우 bad_request 반환하게끔!
    }

    //오픈채팅방 신규 생성
    @Transactional
    public ResponseEntity<?> createOpenChatRoom(CreateOpenChatRoomDto createOpenChatRoomDto, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        User loginUser = principalDetails.getUser();
        OpenChatRoom openChatRoom = OpenChatRoom.builder()
                .title(createOpenChatRoomDto.getTitle())
                .description(createOpenChatRoomDto.getDescription())
                .thumbnail(createOpenChatRoomDto.getThumbnail())
                .mainSchool(createOpenChatRoomDto.getMainSkl())
                .country(createOpenChatRoomDto.getHostContr())
                .hostSchool(createOpenChatRoomDto.getHostSkl())
                .profile(Profile.valueOf(createOpenChatRoomDto.getProfile()))
                .representer(loginUser)
                .build();

        JoinOpenChat joinOpenChat = JoinOpenChat.builder()
                .openChatRoom(openChatRoomRepository.save(openChatRoom))
                .user(loginUser)
                .build();
        joinOpenChatRepository.save(joinOpenChat);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //[오픈채팅방] 참여중인 유저 목록 및 알림 여부 조회
    @GetMapping("/{chat_room_id}/users")
    public OpenChatUsersListDto getOpenChatUsersList (Long chat_room_id, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        Optional<OpenChatRoom> openChatRoom = openChatRoomRepository.findById(chat_room_id);
        List<JoinOpenChat> joinOpenChats = joinOpenChatRepository.findJoinOpenChatsById(chat_room_id);

        int participants = joinOpenChats.size(); //참여자수

        List<OpenChatUsersListDto.userListDTO> result = new ArrayList<>();

        if (openChatRoom.get().getProfile() == Profile.REALNAME){ //실명 오픈채팅방(실명 프로필 활용)
            for (int i=0; i<joinOpenChats.size(); i++) {
                result.add(OpenChatUsersListDto.userListDTO.builder()
                        .userId(joinOpenChats.get(i).getUser().getId())
                        .thumbnail(joinOpenChats.get(i).getUser().getProfilePicture())
                        .name(joinOpenChats.get(i).getUser().getName())
                        .build());}

        }
        else //닉네임 오픈채팅방(커뮤니티 프로필 활용)
        {
            for (int i=0; i<joinOpenChats.size(); i++) {
                CommunityProfile communityProfile = communityProfileRepository.findByUserId(joinOpenChats.get(i).getUser().getId());
                result.add(OpenChatUsersListDto.userListDTO.builder()
                        .userId(communityProfile.getUser().getId())
                        .thumbnail(communityProfile.getProfilePicture())
                        .name(communityProfile.getName())
                        .build());
            }

        }

        JoinOpenChat loginUserJoinOpenChat = joinOpenChatRepository.findByOpenChatroomIdAndUserId(chat_room_id, principalDetails.getUser().getId());

        OpenChatUsersListDto openChatUsersListDto = OpenChatUsersListDto.builder()
                .representerId(openChatRoom.get().getRepresenter().getId())
                .loginUserId(principalDetails.getUser().getId())
                .userListDTOList(result)
                .isNoti(loginUserJoinOpenChat.isNoti())
                .profile(openChatRoom.get().getProfile().toString())
                .participants(participants)
                .build();

        return openChatUsersListDto;
    }

    //[오픈채팅방] 유저 개별 프로필 조회 API (실명채팅방/닉네임채팅방)
    @Transactional
    public ResponseEntity<?> getOpenUserProfile(Long chat_room_id, Long user_id)
    {
        Optional<OpenChatRoom> openChatRoom = openChatRoomRepository.findById(chat_room_id);
        if (openChatRoom.get().getProfile() == Profile.REALNAME) //실명채팅방
        {
            return ResponseEntity.ok(getUserRealNameProfile(user_id));
        }
        else //닉네임채팅방
        {
            return ResponseEntity.ok(userProfileService.getUserProfileInfo(user_id));
        }
    }

    //현재 로그인된 유저가 참여중인 오픈채팅 목록 조회
    //TODO: 메세지가 최신순인 순서대로 정렬되지 않는 문제 해결하기
    @Transactional
    public List<OpenChatListDto> readOpenChatList(@AuthenticationPrincipal PrincipalDetails principalDetails){
        User loginUser = principalDetails.getUser(); //현재 로그인된 유저
        List<JoinOpenChat> joinOpenChatList = joinOpenChatRepository.findByUserId(loginUser.getId());
        List<OpenChatListDto> result = new ArrayList<>();

        for (int i=0; i<joinOpenChatList.size(); i++)
        {
            OpenChatRoom openChatRoom = openChatRoomRepository.findById(joinOpenChatList.get(i).getOpenChatRoom().getId())
                    .orElseThrow(() -> new IllegalArgumentException("오픈채팅방을 찾을 수 없습니다."));

            OpenMessage latestMessage = openMessageRepository.findLatestMessage(openChatRoom.getId());

            List<JoinOpenChat> joinOpenChats = joinOpenChatRepository.findJoinOpenChatsById(openChatRoom.getId());

            result.add(OpenChatListDto.builder()
                    .chatRoomId(openChatRoom.getId())
                    .title(openChatRoom.getTitle())
                    .thumbnail(openChatRoom.getThumbnail())
                    .message(latestMessage.getMessage())
                    .createdAt(latestMessage.getCreatedAt())
                    .participants(joinOpenChats.size())
                    .build());
        }

        //TODO: 안읽은 메세지 수 카운트하는 로직 추가

        return result;
    }

    //전체 오픈채팅 목록 조회
    //TODO: 정렬 기준 설정하기
    @Transactional
    public List<OpenChatListDto> readAllOpenChatList(){
        List<OpenChatListDto> result = new ArrayList<>();
        List<OpenChatRoom> openChatRooms = openChatRoomRepository.findAll();

        for (int i=0; i<openChatRooms.size(); i++)
        {
            OpenChatRoom openChatRoom = openChatRoomRepository.findById(openChatRooms.get(i).getId())
                    .orElseThrow(() -> new IllegalArgumentException("오픈채팅방을 찾을 수 없습니다."));

            List<JoinOpenChat> joinOpenChats = joinOpenChatRepository.findJoinOpenChatsById(openChatRoom.getId());

            OpenMessage latestMessage = openMessageRepository.findLatestMessage(openChatRoom.getId());

            result.add(OpenChatListDto.builder()
                    .chatRoomId(openChatRoom.getId())
                    .title(openChatRoom.getTitle())
                    .thumbnail(openChatRoom.getThumbnail())
                    .message(latestMessage.getMessage())
                    .createdAt(latestMessage.getCreatedAt())
                    .participants(joinOpenChats.size())
                    .build());
        }
        return result;
    }

    //신고하기(POST) API
    @Transactional
    public ResponseEntity<?> reportUser(Long user_id, ReportRequestDto reportRequestDto, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        User loginUser = principalDetails.getUser();
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("피신고자 ID 오류" + user_id));

        //TODO: reportFile DTO에서 받아오는 거 말고, 업로드 함수 활용하는 것으로 로직 수정!
        Report report = Report.builder()
                .reporter(loginUser)
                .reported(user)
                .reportReason(ReportReason.valueOf(reportRequestDto.getReportReason()))
                .reportFile(reportRequestDto.getReportFile())
                .build();

        return ResponseEntity.ok(reportRepository.save(report));
    }

    //(Temp) 1:1 채팅 메세지 전송
    @Transactional
    public ResponseEntity<?> sendChatMessage(Long chat_room_id, MessageDto messageDto, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        ChatRoom chatRoom = chatRoomRepository.findById(chat_room_id)
                .orElseThrow(() -> new IllegalArgumentException(chat_room_id + "에 해당하는 채팅방을 찾을 수 없습니다."));

        ChatMessage chatMessage =
                ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .user(principalDetails.getUser())
                        .isFile(false)
                        .message(messageDto.getMessage())
                        .messageType(MessageType.valueOf("TALK"))
                        .build();

        return ResponseEntity.ok(messageRepository.save(chatMessage));
    }

    //(Temp) 오픈 채팅 메세지 전송
    @Transactional
    public ResponseEntity<?> sendOpenChatMessage(Long chat_room_id, MessageDto messageDto, @AuthenticationPrincipal PrincipalDetails principalDetails)
    {
        OpenChatRoom chatRoom = openChatRoomRepository.findById(chat_room_id)
                .orElseThrow(() -> new IllegalArgumentException(chat_room_id + "에 해당하는 채팅방을 찾을 수 없습니다."));

        OpenMessage chatMessage =
                OpenMessage.builder()
                        .openChatRoom(chatRoom)
                        .user(principalDetails.getUser())
                        .isFile(false)
                        .message(messageDto.getMessage())
                        .messageType(MessageType.valueOf("TALK"))
                        .build();

        return ResponseEntity.ok(openMessageRepository.save(chatMessage));
    }

    // 전체 개설된 오픈채팅방 목록 검색
    @Transactional
    public List<OpenChatSearchListDto> searchOpenChatRooms(String search){
        List<OpenChatRoom> openChatRoomList= openChatRoomRepository.findByTitle(search);
        List<OpenChatSearchListDto> result = new ArrayList<>();

        for (int i=0; i<openChatRoomList.size(); i++)
        {
            OpenChatRoom chatRoom = openChatRoomList.get(i);
            List<JoinOpenChat> joinOpenChats = joinOpenChatRepository.findJoinOpenChatsById(chatRoom.getId());

            OpenChatSearchListDto searchListDto = OpenChatSearchListDto.builder()
                    .chatRoomId(chatRoom.getId())
                    .title(chatRoom.getTitle())
                    .thumbnail(chatRoom.getThumbnail())
                    .description(chatRoom.getDescription())
                    .participants(joinOpenChats.size())
                    .build();

            result.add(searchListDto);
        }

        return result;
    }

    // 1:1 채팅 메세지 검색
    @Transactional
    public List<ChatMessage> searchChatMessages(Long chatRoomId, String search){
        return messageRepository.findBySearch(chatRoomId, search);
    }

    //오픈 채팅 메세지 검색
    @Transactional
    public List<OpenMessage> searchOpenMessages(Long openChatRoomId, String search){
        return openMessageRepository.findBySearch(openChatRoomId, search);
    }

}