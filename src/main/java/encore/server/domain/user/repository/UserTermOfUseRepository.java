package encore.server.domain.user.repository;

import encore.server.domain.user.entity.User;
import encore.server.domain.user.entity.UserTermOfUse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserTermOfUseRepository extends JpaRepository<UserTermOfUse, Long> {
  @Modifying
  @Query("UPDATE UserTermOfUse utu SET utu.deletedAt = CURRENT_TIMESTAMP WHERE utu.user = :user AND utu.deletedAt IS NULL")
  void deleteAllByUserAndDeletedAtIsNull(User user);
}
