package encore.server.domain.review.repository;

import encore.server.domain.review.entity.ViewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViewImageRepository extends JpaRepository<ViewImage, Long> {
    List<ViewImage> findByIdBetween(Long start, Long end);
}
