package encore.server.domain.user.repository;

import encore.server.domain.user.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FCMToken, Long> {
}
