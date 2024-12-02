package encore.server.domain.review.repository;

import encore.server.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByTicketIdAndDeletedAtIsNull(Long ticketId);
}
