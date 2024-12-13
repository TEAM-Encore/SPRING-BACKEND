package encore.server.domain.review.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import encore.server.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

}
