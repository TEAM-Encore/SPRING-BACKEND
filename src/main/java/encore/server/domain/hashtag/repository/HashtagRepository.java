package encore.server.domain.hashtag.repository;

import encore.server.domain.hashtag.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}
