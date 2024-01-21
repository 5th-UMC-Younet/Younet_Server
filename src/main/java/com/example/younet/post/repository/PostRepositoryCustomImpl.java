package com.example.younet.post.repository;

import com.example.younet.domain.Category;
import com.example.younet.domain.Post;
import com.example.younet.domain.QPost;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final CategoryRepository categoryRepository;
    @Override
    public List<Post> getPostList(Long categoryId) {
        QPost post=QPost.post;
        Category category=categoryRepository.findById(categoryId).get();
        return queryFactory.select(post) // select *
                .from(post)
                .where(post.category.eq(category)).fetch()
                ;
    }
    @Override
    public List<Post> getPostListWithPageAndOrder(Long offset, int pageSize) {
        QPost post=QPost.post;
        OrderSpecifier<?> orderSpecifier=new OrderSpecifier<>(Order.DESC,post.createdAt);

        return queryFactory.selectFrom(post)
                .offset(offset)
                .limit(pageSize)
                .orderBy(orderSpecifier)
                .fetch();
    }
}
