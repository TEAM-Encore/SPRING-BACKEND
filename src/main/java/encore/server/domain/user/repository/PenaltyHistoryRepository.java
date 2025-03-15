package encore.server.domain.user.repository;

import encore.server.domain.user.entity.PenaltyHistory;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.enumerate.PenaltyStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenaltyHistoryRepository extends JpaRepository<PenaltyHistory, Long> {

  Optional<PenaltyHistory> findByUserAndStatusAndDeletedAtIsNull(User user, PenaltyStatus status);
}
