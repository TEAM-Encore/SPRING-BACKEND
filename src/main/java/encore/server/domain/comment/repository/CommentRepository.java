package encore.server.domain.comment.repository;

import encore.server.domain.comment.entity.Comment;
import encore.server.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndPostAndDeletedAtIsNull(Long id, Post post);

    List<Comment> findAllByPostAndDeletedAtIsNull(Post post);
    Integer countByPostAndDeletedAtIsNull(Post post);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.deletedAt = CURRENT_TIMESTAMP WHERE c.post.id = :postId")
    void softDeleteByPostId(@Param("postId") Long postId);

}
