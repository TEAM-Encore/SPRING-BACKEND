package encore.server.domain.post.repository;

import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostLike;
import encore.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 특정 Post에 대해 liked가 true인 좋아요의 개수를 세는 메서드
    Long countByPostAndLikedTrue(Post post);

    Optional<PostLike> findByUserAndPost(User user, Post post);
    Integer countByPostAndDeletedAtIsNull(Post post);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PostLike pl SET pl.deletedAt = CURRENT_TIMESTAMP WHERE pl.post.id = :postId")
    void softDeleteByPostId(@Param("postId") Long postId);
}
