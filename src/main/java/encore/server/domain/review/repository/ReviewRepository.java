package encore.server.domain.review.repository;

import encore.server.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    Optional<Review> findByTicketIdAndDeletedAtIsNull(Long ticketId);
    Optional<Review> findByIdAndDeletedAtIsNull(Long reviewId);
    List<Review> findByUserIdAndDeletedAtIsNull(Long userId);
}
