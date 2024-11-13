package encore.server.domain.post.repository;

import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PostImage pi WHERE pi.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostImage pi SET pi.deletedAt = CURRENT_TIMESTAMP WHERE pi.post.id = :postId")
    void softDeleteByPostId(@Param("postId") Long postId);


}
