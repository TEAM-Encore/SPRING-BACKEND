package encore.server.domain.user.repository;

import encore.server.domain.user.entity.TermOfUse;
import encore.server.domain.user.enumerate.TermType;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermOfUseRepository extends JpaRepository<TermOfUse, Long> {

  Set<TermOfUse> findAllByDeletedAtIsNullAndIsOptionalFalse();
  long countAllByDeletedAtIsNullAndIsOptionalFalse();

  List<TermOfUse> findAllByDeletedAtIsNullAndTermTypeIn(List<TermType> termTypes);
}
