package encore.server.domain.subscription.repository;

import encore.server.domain.subscription.entity.Subscription;
import encore.server.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

  boolean existsByFollowerAndFollowing(User follower, User following);

  Optional<Subscription> findByFollowerAndFollowing(User follower, User following);

  @EntityGraph(attributePaths = {"following"})
  List<Subscription> findByFollowerOrderByCreatedAtDesc(User follower, Pageable pageable);

  @EntityGraph(attributePaths = {"following"})
  List<Subscription> findByFollowerAndCreatedAtBeforeOrderByCreatedAtDesc(User follower,
      LocalDateTime cursor, Pageable pageable);

  long countByFollowingId(Long followingId);
}
