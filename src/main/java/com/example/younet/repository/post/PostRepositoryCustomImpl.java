package com.example.younet.repository.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl {
    private final JPAQueryFactory queryFactory;
}
