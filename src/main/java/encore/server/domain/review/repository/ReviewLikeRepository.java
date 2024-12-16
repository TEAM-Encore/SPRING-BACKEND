package encore.server.domain.review.repository;

import encore.server.domain.review.entity.Review;
import encore.server.domain.review.entity.ReviewLike;
import encore.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewAndUser(Review review, User user);
    Long countByReviewAndIsLikeTrue(Review review);
    boolean existsByReviewAndUserAndIsLikeTrue(Review review, User user);
}
