package encore.server.domain.review.repository;

import encore.server.domain.review.entity.Review;
import encore.server.domain.review.entity.ReviewLike;
import encore.server.domain.review.enumerate.LikeType;
import encore.server.domain.review.mapping.LikeTypeMapping;
import encore.server.domain.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewAndUser(Review review, User user);
    Long countByReview(Review review);
    Optional<LikeTypeMapping> findLikeTypeByReviewAndUser(Review review, User user);
    long countByReviewAndLikeType(Review review, LikeType likeType);

    @EntityGraph(attributePaths = {"review"})
    @Query("""
    select rl
    from ReviewLike rl
    where rl.user = :user
      and (:cursor is null or rl.id < :cursor)
    order by rl.id desc
    """)
    Slice<ReviewLike> findByUserAndCursor(
        @Param("user") User user,
        @Param("cursor") Long cursor,
        Pageable pageable
    );}
