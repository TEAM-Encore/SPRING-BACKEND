package encore.server.domain.review.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.entity.ReviewLike;
import encore.server.domain.review.enumerate.Tag;
import encore.server.domain.user.entity.User;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static encore.server.domain.review.entity.QReview.review;
import static encore.server.domain.review.entity.QReviewTags.reviewTags;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<Review> findReviewListByCursorAndSearchKeyword(String searchKeyword, Long cursor, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(review.deletedAt.isNull())
            .and(addCursorCondition(cursor));

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            predicate.and(addKeywordCondition(searchKeyword));
        }

        return queryFactory
            .selectFrom(review)
            .where(predicate)
            .orderBy(review.id.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();
    }

    @Override
    public Slice<Review> findReviewListByUserAndCursor(User user,
        Long cursor, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(review.deletedAt.isNull())
            .and(addCursorCondition(cursor))
            .and(review.user.eq(user));

        List<Review> results = queryFactory
            .selectFrom(review)
            .where(predicate)
            .orderBy(review.id.desc())
            .limit(pageable.getPageSize() + 1)   // +1로 다음 페이지 존재 여부 판단
            .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results = results.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

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

    public List<Review> findUserReviews(Long userId, Long reviewId) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(review.user.id.eq(userId))
                .and(review.deletedAt.isNull())
                .and(review.id.isNotNull());

        if (reviewId != null) {
            predicate.and(review.id.ne(reviewId));
        }

        return queryFactory.selectFrom(review)
                .where(predicate)
                .orderBy(review.createdAt.desc())
                .limit(3)
                .fetch();
    }

    public List<Review> findPopularReviews() {
        return queryFactory
                .selectFrom(review)
                .where(review.deletedAt.isNull())
                .orderBy(review.likeCount.desc(), review.createdAt.desc())
                .limit(5)
                .fetch();
    }

    public List<Review> findReviewListByCursor(String searchKeyword, Long cursor, String tag, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(review.deletedAt.isNull())
                .and(addKeywordCondition(searchKeyword))
                .and(addTagCondition(tag))
                .and(addCursorCondition(cursor));

        return queryFactory
                .selectFrom(review)
                .where(predicate)
                .orderBy(getSortOrder(pageable))
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    public List<Review> findByReviewAutoCompleteSuggestions(Long userId, String keyword) {
        return queryFactory
                .selectFrom(review)
                .where(review.user.id.eq(userId)
                        .and(review.deletedAt.isNull())
                        .and(review.title.containsIgnoreCase(keyword)
                                .or(review.reviewData.rating.ratingReview.containsIgnoreCase(keyword))
                                .or(review.reviewData.facility.facilityReview.containsIgnoreCase(keyword))
                                .or(review.reviewData.sound.soundReview.containsIgnoreCase(keyword))
                                .or(review.reviewData.view.viewReview.containsIgnoreCase(keyword))))
                .limit(5)
                .fetch();
    }

    // 정렬 조건 추가
    private OrderSpecifier<?>[] getSortOrder(Pageable pageable) {
        return pageable.getSort().stream()
                .flatMap(order -> {
                    switch (order.getProperty().toLowerCase()) {
                        case "createdat":
                            return Stream.of(new OrderSpecifier<>(Order.DESC, review.id));
                        case "likecount":
                            return Stream.of(
                                    new OrderSpecifier<>(Order.DESC, review.likeCount),
                                    new OrderSpecifier<>(Order.DESC, review.createdAt)
                            );
                        default:
                            return Stream.of(new OrderSpecifier<>(Order.DESC, review.id));
                    }
                })
                .toArray(OrderSpecifier[]::new);
    }

    // keyword 조건 추가
    private BooleanExpression addKeywordCondition(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return review.title.containsIgnoreCase(keyword)
                .or(review.reviewData.rating.ratingReview.containsIgnoreCase(keyword));
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
        if (tag == null || tag.trim().isEmpty()) {
            return null;
        }
        try {
            return review.tags.any().tag.eq(Tag.valueOf(tag));
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ErrorCode.REVIEW_TAG_NOT_FOUND_EXCEPTION);
        }
    }
}
