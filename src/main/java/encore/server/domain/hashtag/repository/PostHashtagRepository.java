package encore.server.domain.hashtag.repository;

import encore.server.domain.hashtag.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
}
