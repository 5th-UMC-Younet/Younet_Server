package com.example.younet.post.repository;

import com.example.younet.domain.*;
import com.example.younet.post.dto.PostResponseDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
public class PostRepositoryCustomImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    QPost post= QPost.post;
    @Override
    public Slice<PostResponseDTO.postListResultDTO> getPostListByDates(Long lastPostId, Long categoryId, Long countryId, Pageable pageable) {
        LocalDateTime date=null;
        if (lastPostId!=null) {
            date = queryFactory.select(post.createdAt).from(post).where(post.id.eq(lastPostId)).fetchOne();
        }
            List<PostResponseDTO.postListResultDTO> content = queryFactory
                .select(Projections.fields(PostResponseDTO.postListResultDTO.class,
                        post.id.as("postId"),
                        post.title.as("title"),
                        post.body.as("body"),
                        post.category.name.as("categoryName")))
                .from(post)
                .where(
                        btPostCreated(date),
                        post.category.id.eq(categoryId),
                        post.country.id.eq(countryId)
                )
                .orderBy(post.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext=isHasNext(content,pageable);

        return new SliceImpl<>(content,pageable,hasNext);
    }

    @Override
    public Slice<PostResponseDTO.postListResultDTO> getPostListByLikes(Long lastPostId, Long categoryId, Long countryId, Pageable pageable) {
        int lastLikesCount = 0;
        if (lastPostId != null) {
            Post lastPost = queryFactory
                    .selectFrom(post)
                    .where(post.id.eq(lastPostId))
                    .fetchOne();

            lastLikesCount = lastPost.getLikesCount();
        }

        List<PostResponseDTO.postListResultDTO> content = queryFactory
                .select(Projections.fields(PostResponseDTO.postListResultDTO.class,
                        post.id.as("postId"),
                        post.title.as("title"),
                        post.body.as("body"),
                        post.category.name.as("categoryName")))
                .from(post)
                .where(
                        post.category.id.eq(categoryId),
                        post.country.id.eq(countryId),
                        ltLikesCountAndId(lastLikesCount, lastPostId)
                )
                .orderBy(post.likesCount.desc(), post.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = isHasNext(content, pageable);

        return new SliceImpl<>(content, pageable, hasNext);
    }
    private BooleanExpression ltLikesCountAndId(int likesCount, Long postId) {
        if (postId == null) {
            return null;
        }
        return post.likesCount.lt(likesCount)
                .or(post.likesCount.eq(likesCount).and(post.id.lt(postId)));
    }

    private BooleanExpression btPostCreated(LocalDateTime date){
        if (date==null)
            return null;
        return post.createdAt.before(date);
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