package encore.server.domain.term.repository;

import encore.server.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long>, TermRepositoryCustom {
}