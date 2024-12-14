package encore.server.domain.review.repository;

import encore.server.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryCustom {
    Optional<Review> findReviewDetail(Long reviewId);
    void updateViewCount(Long reviewId, Long increment);

    List<Review> findPopularReviews();

    List<Review> findReviewListByCursor(Long cursor, String tag, Pageable pageable);
}
