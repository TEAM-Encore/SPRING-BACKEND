package encore.server.domain.post.repository;

import encore.server.domain.post.dto.response.SimplePostRes;
import encore.server.domain.post.entity.Post;
import encore.server.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> findPostsByCursor(Long cursor, String category,
                                 String type, String searchWord, Pageable pageable);
    List<Post> findPostsByHashtag(Long cursor, String hashtag, Pageable pageable);
    Optional<Post> findFetchJoinPostImageAndUserByIdAndDeletedAtIsNull(Long postId);

    List<Post> findByPostAutoCompleteSuggestions(Long userId, String keyword);
}
