package encore.server.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.QPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public class PostCustomRepositoryImpl implements PostCustomRepository {

    @Autowired
    private JPAQueryFactory queryFactory;

    private final QPost post = QPost.post;

    @Override
    public List<Post> findLatestPostsByCursor(Long lastPostId, LocalDateTime lastCreatedAt, int pageSize) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(post.deletedAt.isNotNull()); // 삭제글 제외

        if (lastPostId != null) {
            builder.and(post.id.lt(lastPostId));
        }
        if (lastCreatedAt != null) {
            builder.and(post.createdAt.lt(lastCreatedAt));
        }

        //id랑
        return queryFactory.selectFrom(post)
                .where(builder)
                .orderBy(post.createdAt.desc())
                .limit(pageSize)
                .fetch();
    }
}