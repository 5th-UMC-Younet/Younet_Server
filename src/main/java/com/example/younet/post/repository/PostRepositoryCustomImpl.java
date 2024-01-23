package com.example.younet.post.repository;

import com.example.younet.domain.Category;
import com.example.younet.domain.Country;
import com.example.younet.domain.Post;
import com.example.younet.domain.QPost;
import com.example.younet.post.dto.PostResponseDTO;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final CategoryRepository categoryRepository;
    private final CountryRepository countryRepository;

    //slice
    QPost post= QPost.post;
    @Override
    public Slice<PostResponseDTO.postListResultDTO> getBySlice(Long lastPostId, Long categoryId, Long countryId, Pageable pageable) {
        // db에서 값 가져오기
        Category category=categoryRepository.findById(categoryId).get();
        Country country=countryRepository.findById(countryId).get();
        List<Post> db= queryFactory.selectFrom(post)
                .where(
                        // no offset
                        ltPostId(lastPostId),
                        post.category.eq(category),
                        post.country.eq(country)
                )
                .orderBy(post.createdAt.desc())
                .limit(pageable.getPageSize()+1)
                .fetch();
        //entity to dto
               List<PostResponseDTO.postListResultDTO> content=db.stream().map(
                value -> PostResponseDTO.postListResultDTO.builder()
                        .postId(value.getId())
                        .title(value.getTitle())
                        .body(value.getBody())
                        .categoryName(value.getCategory().getName())
                        .build()
        ).collect(Collectors.toCollection(ArrayList::new));
        // hasNext
        boolean hasNext=false;
        if (content.size()> pageable.getPageSize()){
            hasNext=true;
            content.remove(pageable.getPageSize());
        }
        // return SliceImpl
        return new SliceImpl<>(content,pageable,hasNext);
    }

    private BooleanExpression ltPostId(Long postId){
        if (postId==null)
            return null;
        return post.id.lt(postId);
    }
}
