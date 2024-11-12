package encore.server.domain.hashtag.repository;

import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostHashtag ph SET ph.deletedAt = CURRENT_TIMESTAMP WHERE ph.post.id = :postId")
    void softDeleteByPostId(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"hashtag"})
    List<PostHashtag> findFetchHashtagAllByPostAndDeletedAtIsNull(Post post);

}
