package encore.server.domain.musical.repository;

import encore.server.domain.musical.entity.Musical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MusicalRepository extends JpaRepository<Musical, Long> {
    List<Musical> findByTitleContaining(String keyword);
    Optional<Musical> findByIdAndDeletedAtIsNull(Long id);
    List<Musical> findByTitleAndDeletedAtIsNull(String title);
    List<Musical> findByOpenApiIdIn(List<String> openApiIds);

    @Query("SELECT m FROM Musical m WHERE m.startDate > :now AND m.deletedAt IS NULL ORDER BY m.startDate ASC")
    List<Musical> findUpcomingMusicals(LocalDateTime now);

    boolean existsByTitle(String title);
    boolean existsByOpenApiId(String openApiId);
}
