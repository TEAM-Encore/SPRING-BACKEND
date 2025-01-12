package encore.server.domain.review.repository;

import encore.server.domain.review.entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    Optional<Review> findByTicketIdAndDeletedAtIsNull(Long ticketId);
    Optional<Review> findByIdAndDeletedAtIsNull(Long reviewId);
    List<Review> findByUserIdAndDeletedAtIsNull(Long userId);

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.user u " +
            "JOIN FETCH r.ticket t " +
            "WHERE t.musical.id = :musicalId AND r.deletedAt IS NULL")
    List<Review> findReviewsByMusicalId(@Param("musicalId") Long musicalId);
}
