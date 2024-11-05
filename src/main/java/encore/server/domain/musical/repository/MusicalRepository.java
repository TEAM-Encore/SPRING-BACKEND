package encore.server.domain.musical.repository;

import encore.server.domain.musical.entity.Musical;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicalRepository extends JpaRepository<Musical, Long> {
}
