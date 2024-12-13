package encore.server.domain.review.repository;

import encore.server.domain.review.entity.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
   boolean existsByUserIdAndReviewIdAndDeletedAtIsNull(Long userId, Long reviewId);
}
