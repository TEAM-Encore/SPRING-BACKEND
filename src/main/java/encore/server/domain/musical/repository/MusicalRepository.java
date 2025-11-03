package encore.server.domain.musical.repository;

import encore.server.domain.musical.entity.Musical;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MusicalRepository extends JpaRepository<Musical, Long> {
    Optional<Musical> findByIdAndDeletedAtIsNull(Long id);
    Page<Musical> findByTitleStartingWithAndDeletedAtIsNull(String keyword, Pageable pageable);
    List<Musical> findByOpenApiIdIn(List<String> openApiIds);

    @Query("SELECT m FROM Musical m WHERE m.startDate > :now AND m.deletedAt IS NULL ORDER BY m.startDate ASC")
    List<Musical> findUpcomingMusicals(LocalDateTime now);

    boolean existsByTitle(String title);
    boolean existsByOpenApiId(String openApiId);
}
