package encore.server.domain.post.repository;


import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostLike;
import encore.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
}