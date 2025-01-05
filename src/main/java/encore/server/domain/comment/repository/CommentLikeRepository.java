package encore.server.domain.comment.repository;

import encore.server.domain.comment.entity.Comment;
import encore.server.domain.comment.entity.CommentLike;
import encore.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
    Boolean existsByCommentAndUserAndLikedTrue(Comment comment, User user);
    Long countByCommentAndLiked(Comment comment, boolean b);
}
