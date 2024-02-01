package com.example.younet.post.repository.CustomRepository;

import com.example.younet.domain.*;
import com.example.younet.post.dto.PostResponseDTO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
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
    QImage image=QImage.image;
    QComment comment=QComment.comment;
    QSection section=QSection.section;
    @Override
    public Slice<PostResponseDTO.postListResultDTO> getPostListByDates(Long lastPostId, Long categoryId, Long countryId, Pageable pageable) {
        LocalDateTime date=null;
        if (lastPostId!=null) {
            date = queryFactory.select(post.createdAt).from(post).where(post.id.eq(lastPostId)).fetchOne();
        }
            JPAQuery<PostResponseDTO.postListResultDTO> query = commonCommunityQuery(categoryId, countryId)
                .where(btPostCreated(date))
                .orderBy(post.createdAt.desc());
        return getSliceResult(query,pageable);
    }

    @Override
    public Slice<PostResponseDTO.postListResultDTO> getPostListByLikes(Long lastPostId, Long categoryId, Long countryId, Pageable pageable) {
        long lastLikesCount=0;
        if (lastPostId != null) {
            Post lastPost = queryFactory
                    .selectFrom(post)
                    .where(post.id.eq(lastPostId))
                    .fetchOne();
            lastLikesCount = lastPost.getLikesCount();
        }

        JPAQuery<PostResponseDTO.postListResultDTO> query = commonCommunityQuery(categoryId, countryId)
                .where(ltLikesCountAndId(lastLikesCount, lastPostId))
                .orderBy(post.likesCount.desc(), post.createdAt.desc());
        return getSliceResult(query,pageable);
    }

    @Override
    public Slice<PostResponseDTO.postListResultDTO> getPostList(String keyword, Pageable pageable) {
        JPAQuery<PostResponseDTO.postListResultDTO> query=commonSearchQuery(keyword)
                .orderBy(post.likesCount.desc(),post.createdAt.desc());
        return getSliceResult(query,pageable);
    }

    private BooleanExpression ltLikesCountAndId(Long likesCount, Long postId) {
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

//    private BooleanExpression containsKeyword(String keyword){
//
//    }

    public boolean isHasNext(List<?> content, Pageable pageable) {
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }
        return hasNext;
    }
    private JPAQuery<PostResponseDTO.postListResultDTO> commonCommunityQuery(Long categoryId, Long countryId) {
        return queryFactory
                .select(Projections.fields(PostResponseDTO.postListResultDTO.class,
                        post.id.as("postId"),
                        post.title.as("title"),
                        post.likesCount.as("likesCount"),
                        post.category.name.as("categoryName"),
                        post.createdAt.as("createdAt"),
                        post.introduction.as("bodySample"),
                        ExpressionUtils.as(JPAExpressions.select(image.imageUrl)
                                .from(image)
                                .where(image.name.eq(post.representativeImage)), "imageSampleUrl"),
                        ExpressionUtils.as(JPAExpressions.select(comment.count()).from(comment).where(comment.post.id.eq(post.id)),"commentsCount")
                ))
                .from(post)
                .where(
                        post.category.id.eq(categoryId),
                        post.country.id.eq(countryId)
                );
    }
    private JPAQuery<PostResponseDTO.postListResultDTO> commonSearchQuery(String keyword) {
        return queryFactory
                .select(Projections.fields(PostResponseDTO.postListResultDTO.class,
                        post.id.as("postId"),
                        post.title.as("title"),
                        post.likesCount.as("likesCount"),
                        post.category.name.as("categoryName"),
                        post.createdAt.as("createdAt"),
                        post.introduction.as("bodySample"),
                        ExpressionUtils.as(JPAExpressions.select(image.imageUrl)
                                .from(image)
                                .where(image.name.eq(post.representativeImage)), "imageSampleUrl"),
                        ExpressionUtils.as(JPAExpressions.select(comment.count()).from(comment).where(comment.post.id.eq(post.id)),"commentsCount")
                ))
                .from(post)
                .leftJoin(section).on(section.post.id.eq(post.id))
                .where(
//                        post.category.id.eq(categoryId),
//                        post.country.id.eq(countryId),
                        post.title.containsIgnoreCase(keyword)
                                .or(section.body.containsIgnoreCase(keyword))
                );
    }
    private Slice<PostResponseDTO.postListResultDTO> getSliceResult(JPAQuery<PostResponseDTO.postListResultDTO> query, Pageable pageable) {
        List<PostResponseDTO.postListResultDTO> content = query.limit(pageable.getPageSize() + 1).fetch();
        boolean hasNext = isHasNext(content, pageable);
        return new SliceImpl<>(content, pageable, hasNext);
    }

}