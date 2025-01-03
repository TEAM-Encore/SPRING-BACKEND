package encore.server.domain.user.repository;

import encore.server.domain.user.entity.TermOfUse;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermOfUseRepository extends JpaRepository<TermOfUse, Long> {

  Set<TermOfUse> findAllByDeletedAtIsNullAndIsOptionalFalse();

}
