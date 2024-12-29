package encore.server.domain.musical.repository;

import encore.server.domain.musical.entity.Musical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MusicalRepository extends JpaRepository<Musical, Long> {
    List<Musical> findByTitleContaining(String keyword);
    Optional<Musical> findByIdAndDeletedAtIsNull(Long id);
    List<Musical> findTop8ByIsFeaturedTrueOrderByStartDateAsc();

    @Query("SELECT m FROM Musical m WHERE m.title = :title AND m.deletedAt IS NULL")
    List<Musical> findByTitle(String title);


}
