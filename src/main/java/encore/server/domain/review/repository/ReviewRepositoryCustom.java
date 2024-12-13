package encore.server.domain.review.repository;

import encore.server.domain.review.entity.Review;

import java.util.Optional;

public interface ReviewRepositoryCustom {
    Optional<Review> findReviewDetail(Long reviewId);
}
