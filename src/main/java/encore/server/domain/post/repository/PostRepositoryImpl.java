package encore.server.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import encore.server.domain.post.converter.PostConverter;
import encore.server.domain.post.dto.response.SimplePostRes;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.enumerate.Category;
import encore.server.domain.post.enumerate.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static encore.server.domain.post.entity.QPost.post;
import static encore.server.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<SimplePostRes> findPostsByCursor(Long cursor, String category, String type,
                                                  String searchWord, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(addCursorCondition(cursor))
                .and(addCategoryCondition(category))
                .and(addTypeCondition(type))
                .and(addSearchWordCondition(searchWord))
                .and(post.deletedAt.isNull());

        List<Post> posts = queryFactory.selectFrom(post)
                .where(predicate)
                .leftJoin(post.user, user).fetchJoin()
                .orderBy(getSortOrder(pageable))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();

        List<SimplePostRes> postResponses = posts.stream()
                .limit(pageable.getPageSize())
                .map(post -> PostConverter.toSimplePostRes(post, post.getUser()))
                .collect(Collectors.toList());

        return new SliceImpl<>(postResponses, pageable, hasNext);
    }

    @Override
    public Slice<SimplePostRes> findPostsByHashtag(Long cursor, String hashtag, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(addCursorCondition(cursor))
                .and(addHashtagCondition(hashtag))
                .and(post.deletedAt.isNull());

        List<Post> posts = queryFactory.selectFrom(post)
                .where(predicate)
                .leftJoin(post.user, user).fetchJoin()
                .orderBy(getSortOrder(pageable))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();

        List<SimplePostRes> postResponses = posts.stream()
                .limit(pageable.getPageSize())
                .map(post -> PostConverter.toSimplePostRes(post, post.getUser()))
                .collect(Collectors.toList());

        return new SliceImpl<>(postResponses, pageable, hasNext);
    }

    private OrderSpecifier<?>[] getSortOrder(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    switch (order.getProperty().toLowerCase()) {
                        case "createdat":
                            return new OrderSpecifier<>(Order.DESC, post.id);
                        case "likecount":
                            return new OrderSpecifier<>(Order.DESC, post.likeCount);
                        default:
                            return new OrderSpecifier<>(Order.DESC, post.id);
                    }
                })
                .toArray(OrderSpecifier[]::new);
    }

    // cursor 뒤인지 확인(id 기준)
    private BooleanExpression addCursorCondition(Long cursor) {
        if (cursor != null) {
            return post.id.lt(cursor);
        }
        return null;
    }

    private BooleanExpression addCategoryCondition(String category) {
        if (category != null && !category.isEmpty()) {
            return post.category.eq(Category.valueOf(category));
        }
        return null;
    }

    private BooleanExpression addTypeCondition(String type) {
        if (type != null && !type.isEmpty()) {
            return post.postType.eq(PostType.valueOf(type));
        }
        return null;
    }

    private BooleanExpression addSearchWordCondition(String searchWord) {
        if (searchWord != null && !searchWord.isEmpty()) {
            return post.title.containsIgnoreCase(searchWord)
                    .or(post.content.containsIgnoreCase(searchWord));
        }
        return null;
    }

    private BooleanExpression addHashtagCondition(String hashtag) {
        if (hashtag != null && !hashtag.isEmpty()) {
            return post.postHashtags.any().hashtag.name.eq(hashtag);
        }
        return null;
    }
}