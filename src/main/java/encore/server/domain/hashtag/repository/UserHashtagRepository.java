package encore.server.domain.hashtag.repository;

import encore.server.domain.hashtag.entity.UserHashtag;
import encore.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserHashtagRepository extends JpaRepository<UserHashtag, Long> {
    Optional<UserHashtag> findByUserAndHashtagName(User user, String name);
    Long countByUser(User user);
    Optional<UserHashtag> findByIdAndUser(Long id, User user);
}
