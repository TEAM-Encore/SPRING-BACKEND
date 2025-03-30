package encore.server.domain.user.repository;

import encore.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"userTermOfUses"})
    Optional<User> findFetchJoinUserTermOfUseByEmail(String email);
    Boolean existsByNickName(String nickName);
    Boolean existsByEmail(String email);

    List<User> findAllByDeletedAtIsNull();
}
