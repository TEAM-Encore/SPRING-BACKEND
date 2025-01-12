package encore.server.domain.user.repository;

import encore.server.domain.user.entity.User;
import encore.server.domain.user.entity.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

  @Modifying
  @Query("DELETE FROM UserKeyword ukw WHERE ukw.user = :user AND ukw.deletedAt IS NULL")
  void deleteAllByUserAndDeletedAtIsNull(User user);
}
