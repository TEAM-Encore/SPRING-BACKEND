package encore.server.domain.hashtag.repository;

import encore.server.domain.hashtag.entity.Hashtag;
import encore.server.domain.hashtag.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);

    List<Hashtag> findByNameInAndDeletedAtIsNull(List<String> hashTagNames);
}
