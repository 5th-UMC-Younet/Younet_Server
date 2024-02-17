package com.example.younet.alarm.repository.CustomRepository;

import com.example.younet.alarm.dto.AlarmResponseDTO;
import com.example.younet.domain.QChatAlarm;
import com.example.younet.domain.QChatRequest;
import com.example.younet.domain.QCommonAlarm;
import com.example.younet.domain.QPost;
import com.example.younet.domain.enums.Profile;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommonAlarmRepositoryCustomImpl implements CommonAlarmRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    QCommonAlarm commonAlarm=QCommonAlarm.commonAlarm;
    QChatAlarm chatAlarm=QChatAlarm.chatAlarm;
    QChatRequest chatRequest=QChatRequest.chatRequest;
    QPost post=QPost.post;
    @Override
    public Slice<AlarmResponseDTO.commonAlarmListResultDTO> getCommonAlarmList(Long lastAlarmId, Long receiverId, Pageable pageable){
        LocalDateTime date=null;
        if (lastAlarmId!=null)
            date=queryFactory.select(commonAlarm.createdAt).from(commonAlarm).where(commonAlarm.id.eq(lastAlarmId)).fetchOne();
        List<AlarmResponseDTO.commonAlarmListResultDTO> content=
                queryFactory
                        .select(Projections.fields(AlarmResponseDTO.commonAlarmListResultDTO.class,
                                commonAlarm.id.as("alarmId"),
                                commonAlarm.alarmType.as("alarmType"),
                                commonAlarm.actorName.as("actorName"),
                                commonAlarm.postId.as("postId"),
                                commonAlarm.createdAt.as("createdAt"),
                                Expressions.cases()
                                        .when(commonAlarm.postId.isNotNull())
                                            .then(
                                                    JPAExpressions.select(post.title)
                                                            .from(post)
                                                            .where(post.id.eq(commonAlarm.postId))
                                            )
                                        .otherwise((String) null).as("postTitle")
                        ))
                        .from(commonAlarm)
                        .where(
                            btPostCreated(date),
                            commonAlarm.isConfirmed.eq(false),
                            commonAlarm.receiver.id.eq(receiverId)
                        )
                        .orderBy(commonAlarm.createdAt.desc())
                        .limit(pageable.getPageSize()+1)
                        .fetch();
        boolean hasNext=isHasNext(content,pageable);
        return new SliceImpl<>(content,pageable,hasNext);

    }

    @Override
    public Slice<AlarmResponseDTO.chatAlarmListResultDTO> getChatAlarmList(Long lastChatAlarmId, Long receiverId, Pageable pageable) {
        LocalDateTime date=null;
        if (lastChatAlarmId!=null)
            date=queryFactory.select(chatAlarm.createdAt).from(chatAlarm).where(chatAlarm.id.eq(lastChatAlarmId)).fetchOne();
        List<AlarmResponseDTO.chatAlarmListResultDTO> content=queryFactory.select(
                Projections.fields(AlarmResponseDTO.chatAlarmListResultDTO.class,
                        chatAlarm.id.as("chatAlarmId"),
                        Expressions.as(
                                new CaseBuilder()
                                        .when(chatRequest.profile.eq(Profile.REALNAME))
                                            .then(chatRequest.requester.name)
                                        .otherwise(chatRequest.requester.nickname),
                                "requesterName"
                        ),
                        chatAlarm.createdAt.as("createdAt")
                        ))
                .from(chatAlarm)
                .where(
                        btPostCreated(date),
                        chatAlarm.receiver.id.eq(receiverId),
                        chatAlarm.isConfirmed.eq(false)
                )
                .limit(pageable.getPageSize()+1)
                .fetch();
        boolean hasNext=isHasNext(content,pageable);
        return new SliceImpl<>(content,pageable,hasNext);
    }

    private BooleanExpression btPostCreated(LocalDateTime date){
        if (date==null)
            return null;
        return commonAlarm.createdAt.before(date);
    }
    public boolean isHasNext(List<?> content, Pageable pageable) {
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }
        return hasNext;
    }

}
