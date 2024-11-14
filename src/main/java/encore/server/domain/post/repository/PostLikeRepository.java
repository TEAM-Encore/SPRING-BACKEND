package encore.server.domain.post.repository;

import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Integer countByPostAndDeletedAtIsNull(Post post);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostLike pl SET pl.deletedAt = CURRENT_TIMESTAMP WHERE pl.post.id = :postId")
    void softDeleteByPostId(@Param("postId") Long postId);
}
