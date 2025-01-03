package encore.server.domain.review.repository;

import encore.server.domain.review.entity.Review;
import encore.server.domain.review.entity.ReviewReport;
import encore.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
     boolean existsByReporterAndReview(User user, Review review);
}
