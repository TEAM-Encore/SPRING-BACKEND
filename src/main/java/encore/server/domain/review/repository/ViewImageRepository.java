package encore.server.domain.review.repository;

import encore.server.domain.review.entity.ViewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ViewImageRepository extends JpaRepository<ViewImage, Long> {
    List<ViewImage> findByIdBetween(Long start, Long end);

    @Query(value = "SELECT * FROM view_image ORDER BY RAND() LIMIT 4", nativeQuery = true)
    List<ViewImage> findRandom4();
}
