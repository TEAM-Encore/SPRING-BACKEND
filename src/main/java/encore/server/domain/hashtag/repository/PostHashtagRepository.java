package encore.server.domain.hashtag.repository;

import encore.server.domain.hashtag.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostHashtag ph SET ph.deletedAt = CURRENT_TIMESTAMP WHERE ph.post.id = :postId")
    void softDeleteByPostId(@Param("postId") Long postId);

}
