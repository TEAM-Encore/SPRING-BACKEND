package encore.server.domain.comment.repository;

import encore.server.domain.comment.entity.Comment;
import encore.server.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndPostAndDeletedAtIsNull(Long id, Post post);
}
