package encore.server.domain.review.repository;

import encore.server.domain.review.entity.Review;
import encore.server.domain.review.entity.ReviewLike;
import encore.server.domain.review.enumerate.LikeType;
import encore.server.domain.review.mapping.LikeTypeMapping;
import encore.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewAndUser(Review review, User user);
    Long countByReview(Review review);
    Optional<LikeTypeMapping> findLikeTypeByReviewAndUser(Review review, User user);
    long countByReviewAndLikeType(Review review, LikeType likeType);
}
