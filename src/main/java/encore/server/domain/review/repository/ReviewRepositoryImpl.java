package encore.server.domain.review.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.enumerate.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static encore.server.domain.review.entity.QReview.review;
import static encore.server.domain.review.entity.QReviewTags.reviewTags;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Optional<Review> findReviewDetail(Long reviewId) {
        Review fetchedReview = queryFactory
                .selectFrom(review)
                .where(review.id.eq(reviewId)
                        .and(review.deletedAt.isNull()))
                .leftJoin(review.tags, reviewTags).fetchJoin()
                .fetchOne();

        return Optional.ofNullable(fetchedReview);
    }

    public void updateViewCount(Long reviewId, Long increment) {
        queryFactory
                .update(review)
                .where(review.id.eq(reviewId))
                .set(review.viewCount, review.viewCount.add(increment))
                .execute();
    }

    public List<Review> findPopularReviews() {
        return queryFactory
                .selectFrom(review)
                .where(review.deletedAt.isNull())
                .orderBy(review.likeCount.desc())
                .limit(5)
                .fetch();
    }

    public List<Review> findReviewListByCursor(Long cursor, String tag, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(review.deletedAt.isNull())
                .and(addCursorCondition(cursor))
                .and(addTagCondition(tag));

        return queryFactory
                .selectFrom(review)
                .where(predicate)
                .orderBy(getSortOrder(pageable))
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    // 정렬 조건 추가
    private OrderSpecifier<?>[] getSortOrder(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    switch (order.getProperty().toLowerCase()) {
                        case "createdat":
                            return new OrderSpecifier<>(Order.DESC, review.id);
                        case "likecount":
                            return new OrderSpecifier<>(Order.DESC, review.likeCount);
                        default:
                            return new OrderSpecifier<>(Order.DESC, review.id);
                    }
                })
                .toArray(OrderSpecifier[]::new);
    }

    // cursor 뒤인지 확인(id 기준)
    private BooleanExpression addCursorCondition(Long cursor) {
        if (cursor != null) {
            return review.id.lt(cursor);
        }
        return null;
    }

    // tag 조건 추가
    private BooleanExpression addTagCondition(String tag) {
        if (tag != null && !tag.isEmpty()) {
            return review.tags.any().tag.eq(Tag.valueOf(tag));
        }
        return null;
    }
}
