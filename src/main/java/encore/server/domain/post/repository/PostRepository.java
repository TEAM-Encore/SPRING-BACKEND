package encore.server.domain.post.repository;

import encore.server.domain.post.entity.Post;
import encore.server.domain.post.enumerate.Category;
import encore.server.domain.post.enumerate.PostType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Post p " +
                    "SET p.category = :category, p.postType = :postType, p.title = :title, " +
                    "p.content = :content, p.isNotice = :isNotice, p.isTemporarySave = :isTemporarySave, " +
                    "p.modifiedAt = CURRENT_TIMESTAMP " +
                    "WHERE p.id = :id"
    )
    int updatePost(
            @Param("category") Category category,
            @Param("postType") PostType postType,
            @Param("title") String title,
            @Param("content") String content,
            @Param("isNotice") boolean isNotice,
            @Param("isTemporarySave") boolean isTemporarySave,
            @Param("id") Long id
    );

    @Modifying
    @Query("UPDATE Post p SET p.deletedAt = CURRENT_TIMESTAMP WHERE p.id = :postId")
    void softDeleteByPostId(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"postImages", "user"})
    Optional<Post> findFetchJoinPostImageAndUserByIdAndDeletedAtIsNull(Long postId);


    Optional<Post> findByIdAndDeletedAtIsNull(Long id);
}
